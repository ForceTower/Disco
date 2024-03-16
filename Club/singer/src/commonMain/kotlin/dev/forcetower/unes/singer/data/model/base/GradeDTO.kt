package dev.forcetower.unes.singer.data.model.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GradeDTO(
    val ordinal: Int,
    @SerialName("nome")
    val name: String,
    @SerialName("nomeResumido")
    val nameShort: String,
    @SerialName("data")
    val date: String? = null,
    @SerialName("peso")
    val weight: Int,
    @SerialName("nota")
    val grade: GradeValueDTO? = null
)

@Serializable
internal data class GradeValueDTO(
    @SerialName("valor")
    val value: Double? = null
)