package dev.forcetower.unes.reactor.domain.dto.auth

data class RegisterPasskeyFinishRequest(
    val flowId: String,
    val credential: String
)
