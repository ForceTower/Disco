package dev.forcetower.unes.club.data.model.remote.edge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceAccessTokenDTO(
    @SerialName("accessToken")
    val accessToken: String
)
