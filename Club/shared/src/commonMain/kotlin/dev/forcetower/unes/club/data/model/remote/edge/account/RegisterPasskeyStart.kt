package dev.forcetower.unes.club.data.model.remote.edge.account

import kotlinx.serialization.Serializable

@Serializable
data class RegisterPasskeyStart(
    val flowId: String,
    val create: String
)
