package dev.forcetower.unes.singer.data.model.base

import dev.forcetower.unes.singer.data.model.dto.aggregators.Items
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class EvaluationDTO(
    @SerialName("nome")
    val name: String? = null,
    @SerialName("avaliacoes")
    val grades: Items<GradeDTO>? = null
)