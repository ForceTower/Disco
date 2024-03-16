package dev.forcetower.unes.singer.data.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClassTime(
    val id: Long,
    @SerialName("dia")
    val day: Int,
    @SerialName("inicio")
    val start: String,
    @SerialName("fim")
    val end: String
)