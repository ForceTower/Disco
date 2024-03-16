package dev.forcetower.unes.singer.data.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClassSpace(
    val id: Long,
    @SerialName("tipo")
    val type: String? = null,  // D.A. DE ECOMP ????????????
    @SerialName("pavilhao")
    val campus: String, // UEFS
    @SerialName("numero")
    val location: String, // MT58
    @SerialName("localizacao")
    val modulo: String  // Modulo 5
)