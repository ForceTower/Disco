package dev.forcetower.unes.club.domain.model.account

data class RegisterPasskeyFlowStart(
    val flowId: String,
    val register: PasskeyRegister
)