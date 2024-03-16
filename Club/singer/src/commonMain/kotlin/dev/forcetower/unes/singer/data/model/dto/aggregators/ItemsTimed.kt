package dev.forcetower.unes.singer.data.model.dto.aggregators

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ItemsTimed<T>(
    @SerialName("maisAntigos")
    val nextPage: Linker? = null,
    @SerialName("maisRecentes")
    val previousPage: Linker? = null,
    @SerialName("itens")
    val items: List<T>
)