package dev.forcetower.unes.club.domain.model.account

import kotlinx.serialization.Serializable

@Serializable
data class Rp(val name: String, val id: String)
@Serializable
data class User(val name: String, val displayName: String, val id: String)
@Serializable
data class PublicKeyCredParam(val alg: Int, val type: String)
@Serializable
data class ExcludedCredential(val type: String, val id: String)
@Serializable
data class AuthenticatorSelection(
    val authenticatorAttachment: String? = null,
    val requireResidentKey: Boolean? = null,
    val residentKey: String? = null,
    val userVerification: String? = null
)
@Serializable
data class Extensions(val credProps: Boolean)

@Serializable
data class PublicKey(
    val rp: Rp,
    val user: User,
    val challenge: String,
    val pubKeyCredParams: List<PublicKeyCredParam>,
    val timeout: Int,
    val excludeCredentials: List<ExcludedCredential>,
    val authenticatorSelection: AuthenticatorSelection,
    val attestation: String,
    val extensions: Extensions
)

@Serializable
data class PasskeyRegister(
    val publicKey: PublicKey
)