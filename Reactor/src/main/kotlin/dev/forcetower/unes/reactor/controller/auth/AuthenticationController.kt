package dev.forcetower.unes.reactor.controller.auth

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.yubico.webauthn.data.AuthenticatorAssertionResponse
import com.yubico.webauthn.data.AuthenticatorAttestationResponse
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs
import com.yubico.webauthn.data.PublicKeyCredential
import dev.forcetower.unes.reactor.data.model.PasskeyAssert
import dev.forcetower.unes.reactor.domain.dto.auth.BasicLoginProvider
import dev.forcetower.unes.reactor.domain.dto.auth.LoginRequest
import dev.forcetower.unes.reactor.domain.dto.auth.LoginResponse
import dev.forcetower.unes.reactor.domain.dto.auth.PasskeyFinishAssertionRequest
import dev.forcetower.unes.reactor.domain.dto.auth.PasskeyStartAssertionRequest
import dev.forcetower.unes.reactor.domain.dto.auth.PasskeyStartAssertionResponse
import dev.forcetower.unes.reactor.domain.dto.auth.RegistrationFailedResponse
import dev.forcetower.unes.reactor.data.repository.RoleRepository
import dev.forcetower.unes.reactor.data.repository.UserRepository
import dev.forcetower.unes.reactor.service.security.auth.AuthTokenService
import dev.forcetower.unes.reactor.service.security.auth.AuthorizationService
import dev.forcetower.unes.reactor.service.security.webauthn.MemoryLoginPasskeyStore
import dev.forcetower.unes.reactor.service.snowpiercer.SnowpiercerAuthService
import dev.forcetower.unes.reactor.utils.base64.YubicoUtils
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
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
    private val cache: MemoryLoginPasskeyStore,
    private val mapper: ObjectMapper
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

    @GetMapping("/login/passkey/assertion/start")
    suspend fun startAssertion(): ResponseEntity<PasskeyStartAssertionResponse> {
        val request = authorizationService.startAssertion()
        val uuid = UUID.randomUUID().toString()
        cache.create(uuid, request)
        val requestJson = request.toCredentialsGetJson()
        val data = mapper.readValue(requestJson, PasskeyAssert::class.java)
        return ResponseEntity.ok(PasskeyStartAssertionResponse(uuid, data))
    }

    @PostMapping("/login/passkey/assertion/finish")
    suspend fun finishAssertion(@RequestBody @Valid body: PasskeyFinishAssertionRequest): ResponseEntity<*> {
        val request = cache.fetch(body.flowId)
            ?: return ResponseEntity.badRequest().body(RegistrationFailedResponse("Login failed"))

        val ref = object : TypeReference<PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs>>() {}
        val credential = mapper.readValue(body.credential, ref)

        try {
            val result = authorizationService.finishAssertion(request, credential)
            if (!result.isSuccess) {
                return ResponseEntity.status(401).body(RegistrationFailedResponse("Login failed"))
            }

            val userId = YubicoUtils.toUUID(result.credential.userHandle)
            val user = users.findById(userId)
                ?: return ResponseEntity.status(401).body(RegistrationFailedResponse("Login failed"))

            val authorities = roles.findRolesByUserId(userId)
            val accessToken = tokenService.generateToken(user, authorities, 129600F)
            return ResponseEntity.ok(LoginResponse(accessToken))
        } catch (error: Exception) {
            return ResponseEntity.badRequest().body(RegistrationFailedResponse("Login failed"))
        }
    }
}