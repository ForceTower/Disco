package dev.forcetower.unes.reactor.controller.auth

import dev.forcetower.unes.reactor.data.entity.User
import dev.forcetower.unes.reactor.domain.dto.auth.PasskeyRegisterService
import dev.forcetower.unes.reactor.domain.dto.auth.RegisterPasskeyFinishRequest
import dev.forcetower.unes.reactor.domain.dto.auth.RegisterPasskeyStartResponse
import dev.forcetower.unes.reactor.service.security.webauthn.MemoryRegisterPasskeyStore
import dev.forcetower.unes.reactor.utils.base64.YubicoUtils
import jakarta.validation.Valid
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("api/passkeys")
class PasskeyController(
    private val passkeyService: PasskeyRegisterService,
    private val cache: MemoryRegisterPasskeyStore
) {
    private val logger = LoggerFactory.getLogger(PasskeyController::class.java)

    @GetMapping("/register/start")
    suspend fun register(): ResponseEntity<RegisterPasskeyStartResponse> {
        val user = ReactiveSecurityContextHolder.getContext().awaitSingleOrNull()?.authentication?.principal as? User
        if (user == null) {
            logger.error("There should exist a user when registering a passkey.")
            return ResponseEntity.badRequest().body(RegisterPasskeyStartResponse("", ""))
        }

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
        passkeyService.finish(user, request, body.credential)

        return ResponseEntity.ok().body(Unit)
    }
}