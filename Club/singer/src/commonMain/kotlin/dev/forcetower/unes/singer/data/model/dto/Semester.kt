package dev.forcetower.unes.singer.data.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Semester(
    val id: Long,
    @SerialName("codigo")
    val code: String,
    @SerialName("descricao")
    val description: String,
    @SerialName("inicio")
    val start: String,
    @SerialName("fim")
    val end: String
)