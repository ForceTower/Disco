package dev.forcetower.unes.singer.data.model.dto.aggregators

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Linker(
    @SerialName("\$link")
    val link: Link
)

@Serializable
internal data class Link (
    val href: String
)

internal fun Linker.idLong(): Long {
    return link.href.split("/").last().toLong()
}