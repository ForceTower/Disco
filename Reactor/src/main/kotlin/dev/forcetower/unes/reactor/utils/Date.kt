package dev.forcetower.unes.reactor.utils

import java.time.ZonedDateTime

fun String.parseZonedDateTime(): ZonedDateTime? = runCatching {
    ZonedDateTime.parse(this)
}.getOrNull()