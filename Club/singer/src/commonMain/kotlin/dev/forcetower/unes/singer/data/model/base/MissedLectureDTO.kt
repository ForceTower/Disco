package dev.forcetower.unes.singer.data.model.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MissedLectureDTO(
    val id: Long,
    @SerialName("abonada")
    val accredited: Boolean,
    @SerialName("retroativa")
    val retroactive: Boolean,
    @SerialName("aula")
    val lecture: LectureDTO
)