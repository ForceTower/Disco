package dev.forcetower.unes.club.data.storage.database.dao

import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.data.storage.database.Grade
import dev.forcetower.unes.club.extensions.hasGrade
import dev.forcetower.unes.club.util.date.parseZonedDateTime
import dev.forcetower.unes.singer.data.model.dto.ClassEvaluation
import kotlinx.datetime.toInstant

class GradeDao(
    private val database: GeneralDatabase
) {
    fun putGradesNewWay(classId: Long, evaluations: List<ClassEvaluation>, notify: Boolean) {
        evaluations.forEach { evaluation ->
            val grades = evaluation.grades
            val named = grades.groupBy { it.name }
            val remapped = named.entries.map { entry ->
                if (entry.value.size == 1) {
                    entry.value[0]
                } else {
                    // Some disciplines still shows more than one practice.
                    // this could be removed if we show all with the same name to the user,
                    // but that would trigger notifications to classes we don't need.
                    // There are 4 solutions:
                    // - remove date changes notifications, and show everything
                    // - remove date changes notifications, show only one grade, but date might be incorrect
                    // - show everything
                    // - show only one, but date might be incorrect
                    // for now, UNES will use option 4, prioritizing earlier dates (better study for early test)
                    entry.value.minByOrNull {
                        when {
                            it.value != null -> Int.MIN_VALUE
                            it.date != null -> {
                                val (date, offset) = parseZonedDateTime(it.date!!)
                                date.toInstant(offset).epochSeconds.toInt()
                            }
                            else -> Int.MAX_VALUE
                        }
                    }!!
                }
            }

            remapped.forEach { grade ->
                println("Attempt to insert ${evaluation.name?.trim()} ${grade.name.trim()} ${grade.value}")
                val current = getNamedGradeDirect(classId, "${grade.nameShort.trim()} - ${grade.name.trim()}", evaluation.name?.trim().hashCode())
                println("Attempt to override ${current?.name} ${current?.groupingName} ${current?.grade}")
                if (current == null) {
                    val notified = if (grade.hasGrade()) 3L else 1L
                    insert(
                        Grade(
                            id = 0L,
                            classId = classId,
                            name = "${grade.nameShort.trim()} - ${grade.name.trim()}",
                            notified = if (notify) notified else 0,
                            grade = grade.value?.toString(),
                            grouping = evaluation.name?.trim().hashCode().toLong(),
                            groupingName = evaluation.name?.trim() ?: "Notas",
                            date = grade.date?.trim()
                        )
                    )
                } else {
                    var shouldUpdate = true
                    val score = grade.value?.toString() ?: ""
                    var next = current
                    if (current.hasGrade() && grade.hasGrade() && score != current.grade) {
                        next = current.copy(
                            notified = 4,
                            grade = score,
                            date = grade.date?.trim()
                        )
                    } else if (!current.hasGrade() && grade.hasGrade()) {
                        next = current.copy(
                            notified = 3,
                            grade = score,
                            date = grade.date?.trim()
                        )
                    } else if (!current.hasGrade() && !grade.hasGrade() && current.date != grade.date) {
                        next = current.copy(
                            notified = 2,
                            date = grade.date?.trim()
                        )
                    } else {
                        shouldUpdate = false
                        println("No changes detected between ${current.name} ${current.grouping} and ${grade.name} ${evaluation.name.hashCode()}")
                    }

                    if (current.groupingName != evaluation.name?.trim()) {
                        shouldUpdate = true
                        next = next.copy(groupingName = evaluation.name?.trim() ?: "Notas")
                    }

                    next = next.copy(notified = if (notify) next.notified else 0)
                    if (shouldUpdate) update(next)
                }
            }
        }
    }

    private fun insert(grade: Grade) {
        database.gradeQueries.insertIgnore(
            grade.id,
            grade.classId,
            grade.name,
            grade.date,
            grade.grade,
            grade.grouping,
            grade.groupingName,
            grade.notified
        )
    }

    private fun update(grade: Grade) {
        database.gradeQueries.updateIgnore(
            grade.id,
            grade.classId,
            grade.name,
            grade.date,
            grade.grade,
            grade.grouping,
            grade.groupingName,
            grade.notified,
            grade.id
        )
    }

    private fun getNamedGradeDirect(classId: Long, name: String, grouping: Int): Grade? {
        return database.gradeQueries.getNamedGrade(classId, name, grouping.toLong()).executeAsOneOrNull()
    }
}