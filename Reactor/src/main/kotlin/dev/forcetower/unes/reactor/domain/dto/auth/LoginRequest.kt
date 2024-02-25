package dev.forcetower.unes.reactor.domain.dto.auth

data class LoginRequest(
    val username: String,
    val password: String
)