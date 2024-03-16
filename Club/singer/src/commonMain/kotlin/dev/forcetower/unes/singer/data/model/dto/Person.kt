package dev.forcetower.unes.singer.data.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val id: Long,
    @SerialName("nome")
    val name: String,
    @SerialName("tipoPessoa")
    val personKind: String,
    val cpf: String?,
    val email: String?
)