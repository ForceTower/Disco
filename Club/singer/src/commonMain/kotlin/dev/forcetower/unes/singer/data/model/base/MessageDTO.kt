package dev.forcetower.unes.singer.data.model.base

import dev.forcetower.unes.singer.data.model.dto.aggregators.Items
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MessageDTO(
    val id: Long,
    @SerialName("descricao")
    val message: String,
    @SerialName("perfilRemetente")
    val profileType: Int,
    @SerialName("timeStamp")
    val timestamp: String,
    @SerialName("remetente")
    val sender: Sender,
    @SerialName("escopos")
    val scopes: Items<MessageScopeDTO>
)

@Serializable
internal data class Sender(
    @SerialName("nome")
    val name: String
)

@Serializable
internal data class MessageScopeDTO(
    val id: Long,
    @SerialName("tipo")
    val type: Int,
    @SerialName("classe")
    val clazz: MessageClassResumed? = null
)

@Serializable
internal data class MessageClassResumed(
    val id: Long,
    @SerialName("descricao")
    val description: String,
    @SerialName("tipo")
    val type: String,
    @SerialName("atividadeCurricular")
    val discipline: DisciplineDTO
)