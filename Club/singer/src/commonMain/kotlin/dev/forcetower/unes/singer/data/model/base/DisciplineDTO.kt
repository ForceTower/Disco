package dev.forcetower.unes.singer.data.model.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DisciplineDTO(
    val id: Long,
    @SerialName("codigo")
    val code: String,
    @SerialName("nome")
    val name: String,
    @SerialName("ementa")
    val program: String? = null,
    @SerialName("cargaHoraria")
    val hours: Int,
    @SerialName("departamento")
    val department: DisciplineDepartment? = null
)

@Serializable
internal data class DisciplineDepartment(
    @SerialName("nome")
    val name: String
)