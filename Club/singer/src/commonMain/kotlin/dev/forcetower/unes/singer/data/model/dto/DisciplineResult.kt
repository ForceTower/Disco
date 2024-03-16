package dev.forcetower.unes.singer.data.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DisciplineResult(
    @SerialName("media")
    val mean: Double? = null,
    @SerialName("totalFaltas")
    val missedClasses: Int,
    @SerialName("descricao")
    val description: String,
    @SerialName("aprovado")
    val approved: Boolean,
    @SerialName("emRevisao")
    val underRevision: Boolean
)