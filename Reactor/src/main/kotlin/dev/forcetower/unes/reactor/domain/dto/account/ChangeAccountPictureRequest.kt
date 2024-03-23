package dev.forcetower.unes.reactor.domain.dto.account

import jakarta.validation.constraints.NotBlank

data class ChangeAccountPictureRequest(
    @field:NotBlank
    val base64: String
)
