package dev.forcetower.unes.club.data.model.remote.edge.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkEmailResponseDTO(
    @SerialName("securityToken")
    val securityToken: String
)