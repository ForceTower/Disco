package dev.forcetower.unes.reactor.controller.auth

import dev.forcetower.unes.reactor.domain.dto.auth.BasicLoginProvider
import dev.forcetower.unes.reactor.domain.dto.auth.LoginRequest
import dev.forcetower.unes.reactor.domain.dto.auth.LoginResponse
import dev.forcetower.unes.reactor.domain.dto.auth.PasskeyFinishAssertionRequest
import dev.forcetower.unes.reactor.domain.dto.auth.PasskeyStartAssertionRequest
import dev.forcetower.unes.reactor.domain.dto.auth.PasskeyStartAssertionResponse
import dev.forcetower.unes.reactor.domain.dto.auth.RegisterRequest
import dev.forcetower.unes.reactor.domain.dto.auth.RegistrationFailedResponse
import dev.forcetower.unes.reactor.data.entity.User
import dev.forcetower.unes.reactor.repository.RoleRepository
import dev.forcetower.unes.reactor.repository.UserRepository
import dev.forcetower.unes.reactor.service.security.auth.AuthTokenService
import dev.forcetower.unes.reactor.service.security.auth.AuthorizationService
import dev.forcetower.unes.reactor.service.security.webauthn.MemoryLoginPasskeyStore
import dev.forcetower.unes.reactor.service.snowpiercer.SnowpiercerAuthService
import dev.forcetower.unes.reactor.service.snowpiercer.SnowpiercerLoginService
import dev.forcetower.unes.reactor.utils.base64.YubicoUtils
import jakarta.validation.Valid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("api/auth")
class AuthenticationController(
    private val users: UserRepository,
    private val roles: RoleRepository,
    private val authorizationService: AuthorizationService,
    private val tokenService: AuthTokenService,
    private val snowpiercerAuth: SnowpiercerAuthService,
    private val cache: MemoryLoginPasskeyStore
) {
    @PostMapping("/login/anonymous")
    suspend fun login(@RequestBody @Valid body: LoginRequest): ResponseEntity<LoginResponse> {
        val (username, password, provider) = body

        val entity = if (provider == BasicLoginProvider.SNOWPIERCER) {
            snowpiercerAuth.login(username, password)
        } else {
            null
        }

        entity ?: return ResponseEntity.badRequest().build()

        val authorities = roles.findRolesByUserId(entity.id)

        val accessToken = tokenService.generateToken(entity, authorities, 129600F)
        return ResponseEntity.ok(LoginResponse(accessToken))
    }

    @GetMapping("/me")
    suspend fun me(): ResponseEntity<*> {
        val principal = ReactiveSecurityContextHolder.getContext().awaitSingleOrNull()?.authentication?.principal
        return ResponseEntity.ok().body(principal)
    }

    @PostMapping("/login/passkey/assertion/start")
    suspend fun startAssertion(@RequestBody @Valid body: PasskeyStartAssertionRequest): ResponseEntity<PasskeyStartAssertionResponse> {
        val request = authorizationService.startAssertion(body)
        val uuid = UUID.randomUUID().toString()
        cache.create(uuid, request)
        return ResponseEntity.ok(PasskeyStartAssertionResponse(uuid, request.toCredentialsGetJson()))
    }

    @PostMapping("/login/passkey/assertion/finish")
    suspend fun finishAssertion(@RequestBody @Valid body: PasskeyFinishAssertionRequest): ResponseEntity<*> {
        val request = cache.fetch(body.flowId)
            ?: return ResponseEntity.badRequest().body(RegistrationFailedResponse("Login failed"))

        val result = authorizationService.finishAssertion(request, body.credential)
        if (!result.isSuccess) {
            return ResponseEntity.badRequest().body(RegistrationFailedResponse("Login failed"))
        }

        val userId = YubicoUtils.toUUID(result.credential.userHandle)
        val user = users.findById(userId)
            ?: return ResponseEntity.badRequest().body(RegistrationFailedResponse("Login failed"))

        val authorities = roles.findRolesByUserId(userId)
        val accessToken = tokenService.generateToken(user, authorities, 129600F)
        return ResponseEntity.ok(LoginResponse(accessToken))
    }
}