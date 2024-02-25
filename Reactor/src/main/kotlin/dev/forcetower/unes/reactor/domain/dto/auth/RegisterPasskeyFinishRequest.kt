package dev.forcetower.unes.reactor.domain.dto.auth

import com.yubico.webauthn.data.AuthenticatorAttestationResponse
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs
import com.yubico.webauthn.data.PublicKeyCredential

data class RegisterPasskeyFinishRequest(
    val flowId: String,
    val credential: PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs>
)
