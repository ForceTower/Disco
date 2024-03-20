package dev.forcetower.unes.club.data.model.remote.edge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceResponseWrapper<T>(
    @SerialName("ok")
    val ok: Boolean,
    @SerialName("data")
    val data: T,
    @SerialName("message")
    val message: String? = null,
    @SerialName("error")
    val error: String? = null
)
