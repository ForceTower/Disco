package dev.forcetower.unes.singer.data.model.base

import dev.forcetower.unes.singer.data.model.dto.aggregators.Items
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class LectureDTO(
    val ordinal: Int,
    @SerialName("situacao")
    val situation: Int,
    @SerialName("data")
    val date: String? = null,
    @SerialName("assunto")
    val subject: String? = null,
    @SerialName("materiaisApoio")
    val materials: Items<LectureMaterialDTO>? = null
)