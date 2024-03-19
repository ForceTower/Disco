package dev.forcetower.unes.club.extensions

import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt

fun Double.truncate(decimals: Int = 1): Double {
    val power = 10.0.pow(decimals.toDouble())
    return floor(this * power) / power
}

fun Double.round(decimals: Int = 1): Double {
    val power = 10.0.pow(decimals.toDouble())
    return (this * power).roundToInt() / power
}