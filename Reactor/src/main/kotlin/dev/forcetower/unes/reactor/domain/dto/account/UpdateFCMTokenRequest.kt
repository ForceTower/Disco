package dev.forcetower.unes.reactor.domain.dto.account

import jakarta.validation.constraints.NotBlank

data class UpdateFCMTokenRequest(
    @field:NotBlank
    val token: String
)
