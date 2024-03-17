package dev.forcetower.unes.singer.data.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DisciplineResult(
    @SerialName("media")
    val mean: Double? = null,
    @SerialName("totalFaltas")
    val missedClasses: Int? = null,
    @SerialName("descricao")
    val description: String? = null,
    @SerialName("aprovado")
    val approved: Boolean? = null,
    @SerialName("emRevisao")
    val underRevision: Boolean
)