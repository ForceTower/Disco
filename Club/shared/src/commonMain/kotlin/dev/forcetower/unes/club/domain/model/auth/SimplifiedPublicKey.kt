package dev.forcetower.unes.club.domain.model.auth

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class SimplifiedPublicKey(
    val challenge: String,
    val timeout: Int,
    val rpId: String,
    val userVerification: String,
    val extensions: Map<String, @Contextual Any?>?
)

@Serializable
data class PasskeyAssert(
    val publicKey: SimplifiedPublicKey
)