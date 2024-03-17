package dev.forcetower.unes.club.domain.model.disciplines

import dev.forcetower.unes.club.data.storage.database.Class
import dev.forcetower.unes.club.data.storage.database.ClassAbsence
import dev.forcetower.unes.club.data.storage.database.ClassGroup
import dev.forcetower.unes.club.data.storage.database.Discipline
import dev.forcetower.unes.club.data.storage.database.Grade
import dev.forcetower.unes.club.data.storage.database.Semester
import dev.forcetower.unes.club.util.date.parseZonedDateTime
import kotlinx.datetime.toInstant

data class SemesterClassData(
    val semester: Semester,
    val classes: List<ClassData>
)

data class ClassData(
    val clazz: Class,
    val discipline: Discipline,
    val groups: List<ClassGroup>,
    val absences: List<ClassAbsence>,
    val grades: List<ProcessedGrade>
)

data class ProcessedGrade(
    val original: Grade,
) {
    val id = original.id
    val name = original.name
    val group = original.grouping
    val groupingName = original.groupingName
    val grade = original.grade
    val dateSeconds: Long?
        get() {
            val date = original.date ?: return null
            val (time, offset) = parseZonedDateTime(date)
            return time.toInstant(offset).epochSeconds
        }
}