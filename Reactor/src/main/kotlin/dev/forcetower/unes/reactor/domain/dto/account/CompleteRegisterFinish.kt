package dev.forcetower.unes.reactor.domain.dto.account

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CompleteRegisterFinish(
    @field:Size(min = 10, max = 10)
    val code: String,
    @field:NotBlank
    val securityToken: String
)