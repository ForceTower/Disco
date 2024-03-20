package dev.forcetower.unes.club.data.model.remote.edge.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceAccountDTO(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("email")
    val email: String? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null
)
