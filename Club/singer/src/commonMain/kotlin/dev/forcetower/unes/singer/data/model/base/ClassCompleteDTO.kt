package dev.forcetower.unes.singer.data.model.base

import dev.forcetower.unes.singer.data.model.dto.Allocation
import dev.forcetower.unes.singer.data.model.dto.aggregators.Items
import dev.forcetower.unes.singer.data.model.dto.aggregators.ItemsPaged
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ClassCompleteDTO(
    val id: Long,
    @SerialName("descricao")
    val description: String,
    @SerialName("tipo")
    val type: String,
    @SerialName("alocacoes")
    val allocations: Items<Allocation>? = null,
    @SerialName("professores")
    val teachers: Items<PersonWrapperDTO>? = null,
    @SerialName("atividadeCurricular")
    val groupDetails: GroupDTO,
    @SerialName("aulas")
    val lectures: ItemsPaged<LectureDTO>? = null
)