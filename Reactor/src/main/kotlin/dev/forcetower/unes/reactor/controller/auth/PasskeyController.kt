package dev.forcetower.unes.reactor.controller.auth

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.yubico.webauthn.data.AuthenticatorAttestationResponse
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs
import com.yubico.webauthn.data.PublicKeyCredential
import dev.forcetower.unes.reactor.data.entity.User
import dev.forcetower.unes.reactor.domain.dto.auth.PasskeyRegisterService
import dev.forcetower.unes.reactor.domain.dto.auth.RegisterPasskeyFinishRequest
import dev.forcetower.unes.reactor.domain.dto.auth.RegisterPasskeyStartResponse
import dev.forcetower.unes.reactor.service.security.webauthn.MemoryRegisterPasskeyStore
import dev.forcetower.unes.reactor.utils.base64.YubicoUtils
import dev.forcetower.unes.reactor.utils.spring.requireUser
import jakarta.validation.Valid
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.boot.json.JacksonJsonParser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@RestController
@RequestMapping("api/passkeys")
class PasskeyController(
    private val passkeyService: PasskeyRegisterService,
    private val cache: MemoryRegisterPasskeyStore,
    private val mapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(PasskeyController::class.java)

    @GetMapping("/register/start")
    suspend fun register(): ResponseEntity<RegisterPasskeyStartResponse> {
        val user = requireUser()
        if (user.email == null) throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "this feature requires an email")
        val request = passkeyService.start(user)
        val uuid = "${user.id}${UUID.randomUUID().toString().substring(0..7)}"
        cache.create(uuid, request)
        return ResponseEntity.ok(RegisterPasskeyStartResponse(uuid, request.toCredentialsCreateJson()))
    }

    @PostMapping("/register/finish")
    suspend fun finish(@RequestBody @Valid body: RegisterPasskeyFinishRequest): ResponseEntity<*> {
        val user = ReactiveSecurityContextHolder.getContext().awaitSingleOrNull()?.authentication?.principal as? User
        if (user == null || !body.flowId.startsWith(user.id.toString())) {
            logger.error("There should exist a user when finishing a passkey.")
            return ResponseEntity.badRequest().body(mapOf("message" to "Failed to register passkey"))
        }

        val request = cache.fetch(body.flowId)
            ?: return ResponseEntity.badRequest().body(mapOf("message" to "Failed to register passkey"))

        if (YubicoUtils.toUUID(request.user.id) != user.id) {
            logger.error("Received user is not the one expected")
            return ResponseEntity.badRequest().body(mapOf("message" to "Failed to register passkey"))
        }

        val ref = object : TypeReference<PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs>>() {}
        val credential = mapper.readValue(body.credential, ref)
        passkeyService.finish(user, request, credential)

        return ResponseEntity.ok().body(Unit)
    }
}