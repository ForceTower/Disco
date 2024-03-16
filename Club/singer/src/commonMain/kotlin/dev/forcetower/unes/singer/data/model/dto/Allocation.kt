package dev.forcetower.unes.singer.data.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Allocation(
    @SerialName("horario")
    val time: ClassTime? = null,
    @SerialName("espacoFisico")
    val space: ClassSpace? = null
) {
    override fun toString(): String {
        return "Day: ${time?.day}. From ${time?.start} until ${time?.end} at ${space?.modulo}, ${space?.location} - ${space?.campus}"
    }
}