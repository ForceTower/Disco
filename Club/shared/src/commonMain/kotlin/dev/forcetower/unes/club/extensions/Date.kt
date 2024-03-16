package dev.forcetower.unes.club.extensions

fun Int.toLongWeekDay(): String {
    return when (this) {
        1 -> "Domingo"
        2 -> "Segunda"
        3 -> "Terça"
        4 -> "Quarta"
        5 -> "Quinta"
        6 -> "Sexta"
        7 -> "Sábado"
        else -> "UNDEFINED"
    }
}

fun Int.toWeekDay(): String {
    return when (this) {
        1 -> "DOM"
        2 -> "SEG"
        3 -> "TER"
        4 -> "QUA"
        5 -> "QUI"
        6 -> "SEX"
        7 -> "SAB"
        else -> "UNDEFINED"
    }
}

fun String.fromWeekDay(): Int {
    return when (this.uppercase()) {
        "DOM" -> 1
        "SEG" -> 2
        "TER" -> 3
        "QUA" -> 4
        "QUI" -> 5
        "SEX" -> 6
        "SAB" -> 7
        else -> 0
    }
}

fun String.createTimeInt(): Int {
    return try {
        val split = this.split(":")
        val hour = split[0].toInt() * 60
        val minute = split[1].toInt()
        hour + minute
    } catch (t: Throwable) {
        0
    }
}
