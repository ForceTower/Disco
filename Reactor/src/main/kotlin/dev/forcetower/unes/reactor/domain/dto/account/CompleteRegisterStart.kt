package dev.forcetower.unes.reactor.domain.dto.account

import jakarta.validation.constraints.Email

data class CompleteRegisterStart(
    @field:Email(message = "This is not a valid email")
    val email: String,
    val dryRun: Boolean? = false
)