package dev.forcetower.unes.singer.data.model.base

import dev.forcetower.unes.singer.data.model.dto.aggregators.Items
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SemesterCompleteDTO(
    val id: Long,
    @SerialName("codigo")
    val code: String,
    @SerialName("descricao")
    val description: String,
    @SerialName("turmas")
    val disciplines: Items<DisciplineCompleteDTO>
)