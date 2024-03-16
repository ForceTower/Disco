package dev.forcetower.unes.singer.data.model.base

import dev.forcetower.unes.singer.data.model.dto.DisciplineResult
import dev.forcetower.unes.singer.data.model.dto.aggregators.Items
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DisciplineCompleteDTO(
    val id: Long,
    @SerialName("limiteFaltas")
    val missLimit: Int? = null,
    @SerialName("atividadeCurricular")
    val activity: DisciplineDTO,
    val classes: Items<ClassCompleteDTO>,
    @SerialName("avaliacoes")
    val evaluations: Items<EvaluationDTO>? = null,
    @SerialName("resultado")
    val result: DisciplineResult? = null
)