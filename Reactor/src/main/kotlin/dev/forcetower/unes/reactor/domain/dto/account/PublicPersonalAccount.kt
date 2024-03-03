package dev.forcetower.unes.reactor.domain.dto.account

data class PublicPersonalAccount(
    val id: String,
    val name: String,
    val email: String?,
    val imageUrl: String?
)