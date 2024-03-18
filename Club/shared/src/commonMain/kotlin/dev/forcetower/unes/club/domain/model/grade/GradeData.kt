package dev.forcetower.unes.club.domain.model.grade

import dev.forcetower.unes.club.data.storage.database.Discipline
import dev.forcetower.unes.club.data.storage.database.Grade

data class GradeData(
    val ref: Grade,
    val discipline: Discipline
)