package dev.forcetower.unes.reactor.domain.dto.auth

import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions

data class RegisterPasskeyStartResponse(
    val flowId: String,
    val create: String
)