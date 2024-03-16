package dev.forcetower.unes.singer.data.model.base

import dev.forcetower.unes.singer.data.model.dto.aggregators.Linker
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class LectureMaterialDTO(
    val id: Long,
    @SerialName("descricao")
    val description: String,
    val url: Linker
)