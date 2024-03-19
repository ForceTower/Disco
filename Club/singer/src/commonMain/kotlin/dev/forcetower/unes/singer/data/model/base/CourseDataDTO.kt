package dev.forcetower.unes.singer.data.model.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseDataDTO(
    @SerialName("id")
    val id: Long,
    @SerialName("nome")
    val name: String,
    @SerialName("nomeResumido")
    val resumed: String
)
