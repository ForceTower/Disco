package dev.forcetower.unes.singer.data.model.base

import dev.forcetower.unes.singer.data.model.dto.Person
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PersonWrapperDTO(
    @SerialName("pessoa")
    val person: Person
)