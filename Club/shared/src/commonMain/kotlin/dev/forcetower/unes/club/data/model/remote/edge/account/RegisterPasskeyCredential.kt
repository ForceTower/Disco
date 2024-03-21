package dev.forcetower.unes.club.data.model.remote.edge.account

import kotlinx.serialization.Serializable

@Serializable
data class RegisterPasskeyCredential(
    val flowId: String,
    val credential: String
)
