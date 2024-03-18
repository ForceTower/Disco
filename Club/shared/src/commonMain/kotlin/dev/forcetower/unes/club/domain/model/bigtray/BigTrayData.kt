package dev.forcetower.unes.club.domain.model.bigtray

import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class BigTrayData(
    val open: Boolean,
    val quota: String,
    val error: Boolean,
    val time: Long,
    val type: String
) {
    companion object {
        const val COFFEE = 1
        const val LUNCH = 2
        const val DINNER = 3

        fun error() = BigTrayData(false, "", true, currentTimeMillis(), "")
        fun closed() = BigTrayData(false, "0", false, currentTimeMillis(), "")
        fun createData(values: List<String>) = BigTrayData(true, values[1].trim(), false, currentTimeMillis(), values[0].trim())
    }
}

private fun currentTimeMillis() = Clock.System.now().toEpochMilliseconds()

fun BigTrayData.getNextMealType(): Int {
    val calendar = this.time.toLocalDateTime()
    val hour = calendar.hour
    val minutes = calendar.minute
    val account = hour * 60 + minutes

    return when {
        account < 9.5 * 60 -> BigTrayData.COFFEE
        account < 14.5 * 60 -> BigTrayData.LUNCH
        account < 20 * 60 -> BigTrayData.DINNER
        else -> BigTrayData.COFFEE
    }
}

private fun Long.toLocalDateTime(): LocalDateTime {
    val instant = Instant.fromEpochMilliseconds(this)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}

fun BigTrayData.getNextMealTime(): String {
    val calendar = this.time.toLocalDateTime()
    val day = calendar.dayOfWeek

    when (getNextMealType()) {
        BigTrayData.COFFEE -> return if (day == DayOfWeek.SUNDAY) "07h30min às 09h00min" else "06h30min às 09h00min"
        BigTrayData.LUNCH -> {
            if (day == DayOfWeek.SUNDAY) return "11h30min às 13h30min"
            return if (day == DayOfWeek.SATURDAY) "11h30min às 14h00min" else "10h30min às 14h00min"
        }
        else -> {
            if (day == DayOfWeek.SUNDAY) return "17h30min às 19h00min"
            return if (day == DayOfWeek.SATURDAY) "17h30min às 19h00min" else "17h30min às 19h30min"
        }
    }
}

fun BigTrayData.isOpen(): Boolean {
    var amount = -1
    try { amount = quota.toInt() } catch (e: Exception) {}
    return open && amount != -1
}

private fun clamp(value: Double, min: Double, max: Double): Double {
    if (value < min) {
        return min
    } else if (value > max) {
        return max
    }
    return value
}

fun BigTrayData.quotaInt() = quota.toIntOrNull() ?: 0

fun BigTrayData.maxQuota(): Int {
    val type = getNextMealType()
    return when (type) {
        BigTrayData.LUNCH -> 1450
        BigTrayData.DINNER -> 490
        else -> 320
    }
}

fun BigTrayData.percentage(): Double {
    try {
        val amount = quota.toDouble()
        val type = getNextMealType()

        return clamp(
            amount / when (type) {
                BigTrayData.LUNCH -> 1450
                BigTrayData.DINNER -> 490
                else -> 320
            },
            0.0,
            1.0
        ) * 100
    } catch (e: Exception) {
        println("Failed to calc percentage: ${e.message}")
    }
    return 0.0
}