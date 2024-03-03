package dev.forcetower.unes.reactor.controller.account

import dev.forcetower.unes.reactor.data.entity.MessagingToken
import dev.forcetower.unes.reactor.data.entity.User
import dev.forcetower.unes.reactor.domain.dto.BaseResponse
import dev.forcetower.unes.reactor.domain.dto.account.PublicPersonalAccount
import dev.forcetower.unes.reactor.domain.dto.account.UpdateFCMTokenRequest
import dev.forcetower.unes.reactor.data.repository.MessagingTokenRepository
import jakarta.validation.Valid
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/account")
class AccountController(
    private val tokens: MessagingTokenRepository
) {
    @GetMapping("/me")
    suspend fun me(): ResponseEntity<BaseResponse> {
        val user = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.principal as User
        return ResponseEntity.ok(
            BaseResponse.ok(
                PublicPersonalAccount(
                    user.id.toString(),
                    user.name,
                    user.email,
                    user.imageUrl
                )
            )
        )
    }

    @PostMapping("/fcm")
    suspend fun updateFcmToken(@RequestBody @Valid body: UpdateFCMTokenRequest): ResponseEntity<BaseResponse> {
        val user = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.principal as User
        val (token) = body
        tokens.save(MessagingToken(token, user.id).apply { setNew() })
        return ResponseEntity.ok(
            BaseResponse.ok("Saved!")
        )
    }
}