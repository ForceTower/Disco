package dev.forcetower.unes.reactor.controller.auth

import dev.forcetower.unes.reactor.domain.dto.auth.PasskeyRegisterService
import dev.forcetower.unes.reactor.domain.dto.auth.RegisterPasskeyFinishRequest
import dev.forcetower.unes.reactor.domain.dto.auth.RegisterPasskeyStartResponse
import dev.forcetower.unes.reactor.domain.entity.User
import dev.forcetower.unes.reactor.service.security.webauthn.MemoryRegisterPasskeyStore
import dev.forcetower.unes.reactor.utils.base64.YubicoUtils
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
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
    fun register(): ResponseEntity<RegisterPasskeyStartResponse> {
        val user = SecurityContextHolder.getContext().authentication?.principal as? User
        if (user == null) {
            logger.error("There should exist a user when registering a passkey.")
            return ResponseEntity.badRequest().body(RegisterPasskeyStartResponse("", ""))
        }

        logger.info("Running... register")
        val request = passkeyService.start(user)
        val uuid = "${user.id}${UUID.randomUUID().toString().substring(0..7)}"
        logger.info("Running... Got request $uuid")
        cache.create(uuid, request)
        logger.info("Running... Created cache...")
        logger.info("Running... Returning ${request.toCredentialsCreateJson()}")
        return ResponseEntity.ok(RegisterPasskeyStartResponse(uuid, request.toCredentialsCreateJson()))
    }

    @PostMapping("/register/finish")
    fun finish(@RequestBody @Valid body: RegisterPasskeyFinishRequest): ResponseEntity<*> {
        val user = SecurityContextHolder.getContext().authentication?.principal as? User
        if (user == null || !body.flowId.startsWith(user.id)) {
            logger.error("There should exist a user when finishing a passkey.")
            return ResponseEntity.badRequest().body(mapOf("message" to "Failed to register passkey"))
        }

        val request = cache.fetch(body.flowId)
            ?: return ResponseEntity.badRequest().body(mapOf("message" to "Failed to register passkey"))

        if (YubicoUtils.toUUIDStr(request.user.id) != user.id) {
            logger.error("Received user is not the one expected")
            return ResponseEntity.badRequest().body(mapOf("message" to "Failed to register passkey"))
        }
        passkeyService.finish(user, request, body.credential)

        return ResponseEntity.ok().body(Unit)
    }
}