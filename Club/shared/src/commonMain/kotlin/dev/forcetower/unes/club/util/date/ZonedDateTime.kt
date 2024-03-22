package dev.forcetower.unes.club.util.date

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.UtcOffset

fun parseZonedDateTime(timestamp: String): Pair<LocalDateTime, UtcOffset> {
    return if (!timestamp.endsWith("Z")) {
        val timezoneStart = timestamp.lastIndexOf("-").takeIf { it > 0 }
            ?: timestamp.lastIndexOf("+").takeIf { it > 0 }
            ?: timestamp.length

        val part = timestamp.substring(0..<timezoneStart)
        val localDateTime = LocalDateTime.parse(part)
        val offset = if (timezoneStart == timestamp.length) {
            UtcOffset.ZERO
        } else {
            val zone = timestamp.substring(timezoneStart..<timestamp.length)
            UtcOffset.parse(zone)
        }
        localDateTime to offset
    } else {
        LocalDateTime.parse(timestamp.replace("Z", "")) to UtcOffset.ZERO
    }
}