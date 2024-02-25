package dev.forcetower.unes.reactor.controller.auth

import dev.forcetower.unes.reactor.domain.dto.auth.LoginRequest
import dev.forcetower.unes.reactor.domain.dto.auth.LoginResponse
import dev.forcetower.unes.reactor.domain.dto.auth.PasskeyFinishAssertionRequest
import dev.forcetower.unes.reactor.domain.dto.auth.PasskeyStartAssertionRequest
import dev.forcetower.unes.reactor.domain.dto.auth.PasskeyStartAssertionResponse
import dev.forcetower.unes.reactor.domain.dto.auth.RegisterRequest
import dev.forcetower.unes.reactor.domain.dto.auth.RegistrationFailedResponse
import dev.forcetower.unes.reactor.domain.entity.User
import dev.forcetower.unes.reactor.repository.RoleRepository
import dev.forcetower.unes.reactor.repository.UserRepository
import dev.forcetower.unes.reactor.service.security.auth.AuthTokenService
import dev.forcetower.unes.reactor.service.security.auth.AuthorizationService
import dev.forcetower.unes.reactor.service.security.webauthn.MemoryLoginPasskeyStore
import dev.forcetower.unes.reactor.utils.base64.YubicoUtils
import jakarta.validation.Valid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
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
    private val authenticationManager: AuthenticationManager,
    private val authorizationService: AuthorizationService,
    private val tokenService: AuthTokenService,
    private val passwordEncoder: PasswordEncoder,
    private val cache: MemoryLoginPasskeyStore
) {
    @PostMapping("/login/password")
    suspend fun login(@RequestBody @Valid body: LoginRequest): ResponseEntity<LoginResponse> {
        val (username, password) = body
        val token = UsernamePasswordAuthenticationToken(username, password)

        val auth = authenticationManager.authenticate(token)
        val user = auth.principal as User

        val accessToken = tokenService.generateToken(user, 129600F)
        return ResponseEntity.ok(LoginResponse(accessToken))
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

        val userId = YubicoUtils.toUUIDStr(result.credential.userHandle)
        val user = withContext(Dispatchers.IO) {
            users.findUserById(userId)
        }

        if (user == null) {
            return ResponseEntity.badRequest().body(RegistrationFailedResponse("Login failed"))
        }

        val accessToken = tokenService.generateToken(user, 129600F)
        return ResponseEntity.ok(LoginResponse(accessToken))
    }

    @PostMapping("/register/password")
    suspend fun register(@RequestBody @Valid body: RegisterRequest): ResponseEntity<*> {
        val user = withContext(Dispatchers.IO) {
            users.findUserByUsername(body.username)
        }
        if (user != null) {
            return ResponseEntity.badRequest().body(RegistrationFailedResponse("username already taken"))
        }

        val roles = withContext(Dispatchers.IO) {
            roles.findBasicRoles()
        }

        val password = passwordEncoder.encode(body.password)
        val next = User("", body.name, body.username, password, body.email)
        val stored = withContext(Dispatchers.IO) {
            users.save(next)
        }

        val accessToken = tokenService.generateToken(stored, 129600F)
        return ResponseEntity.ok(LoginResponse(accessToken))
    }
}