package dev.forcetower.unes.reactor.data.model

data class SimplifiedPublicKey(
    val challenge: String,
    val timeout: Int,
    val rpId: String,
    val userVerification: String,
    val extensions: Map<String, Any?>?
)

data class PasskeyAssert(
    val publicKey: SimplifiedPublicKey
)