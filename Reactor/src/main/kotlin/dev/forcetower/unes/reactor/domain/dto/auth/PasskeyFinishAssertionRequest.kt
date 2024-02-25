package dev.forcetower.unes.reactor.domain.dto.auth

import com.yubico.webauthn.data.AuthenticatorAssertionResponse
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs
import com.yubico.webauthn.data.PublicKeyCredential

data class PasskeyFinishAssertionRequest(
    val flowId: String,
    val credential: PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs>
)