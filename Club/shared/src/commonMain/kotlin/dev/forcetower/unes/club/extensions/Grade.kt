package dev.forcetower.unes.club.extensions

import dev.forcetower.unes.club.data.storage.database.Grade

fun Grade.hasGrade(): Boolean {
    return grade != null &&
        grade.trim().isNotEmpty() &&
        !grade.trim().equals("Não Divulgada", ignoreCase = true) &&
        !grade.trim().equals("-", ignoreCase = true) &&
        !grade.trim().equals("--", ignoreCase = true) &&
        !grade.trim().equals("*", ignoreCase = true) &&
        !grade.trim().equals("**", ignoreCase = true) &&
        !grade.trim().equals("-1", ignoreCase = true)
}

fun Grade.gradeDouble() = grade?.trim()
    ?.replace(",", ".")
    ?.replace("-", "")
    ?.replace("*", "")
    ?.toDoubleOrNull()