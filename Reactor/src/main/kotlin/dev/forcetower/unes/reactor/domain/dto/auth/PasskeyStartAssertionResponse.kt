package dev.forcetower.unes.reactor.domain.dto.auth

data class PasskeyStartAssertionResponse(
    val flowId: String,
    val challenge: String
)