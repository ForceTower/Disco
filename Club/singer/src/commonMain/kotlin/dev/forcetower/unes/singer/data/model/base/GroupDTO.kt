package dev.forcetower.unes.singer.data.model.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GroupDTO(
    val id: Long,
    @SerialName("cargaHoraria")
    val hours: Int,
    @SerialName("ementa")
    val program: String? = null
)