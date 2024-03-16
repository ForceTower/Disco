package dev.forcetower.unes.singer.data.model.dto.aggregators

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ItemsTimed<T>(
    @SerialName("maisAntigos")
    val nextPage: Linker?,
    @SerialName("maisRecentes")
    val previousPage: Linker?,
    @SerialName("itens")
    val items: List<T>
)