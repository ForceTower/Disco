package dev.forcetower.unes.singer.data.model.dto.aggregators

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ItemsPaged<T>(
    @SerialName("proximaPagina")
    val nextPage: Linker?,
    @SerialName("itens")
    val items: List<T>
)