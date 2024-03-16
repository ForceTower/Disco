package dev.forcetower.unes.club.extensions

import dev.forcetower.unes.club.util.string.WordUtils

fun String.removeSeconds(): String {
    return this.split(":").take(2).joinToString(":")
}

fun String.toTitleCase() = WordUtils.toTitleCase(this)