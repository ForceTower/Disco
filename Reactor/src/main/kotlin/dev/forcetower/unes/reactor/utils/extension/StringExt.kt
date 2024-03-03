package dev.forcetower.unes.reactor.utils.extension

import dev.forcetower.unes.reactor.utils.word.WordUtils

fun String.toTitleCase(): String = WordUtils.toTitleCase(this)