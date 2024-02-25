package dev.forcetower.unes.reactor.domain.dto.auth

data class RegisterRequest(
    val name: String,
    val username: String,
    val password: String,
    val email: String
)