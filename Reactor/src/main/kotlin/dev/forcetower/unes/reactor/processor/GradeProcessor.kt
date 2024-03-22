package dev.forcetower.unes.reactor.processor

import dev.forcetower.breaker.model.ClassEvaluation
import dev.forcetower.unes.reactor.data.entity.Grade
import dev.forcetower.unes.reactor.data.entity.Student
import dev.forcetower.unes.reactor.data.entity.StudentClass
import dev.forcetower.unes.reactor.data.repository.GradeRepository
import dev.forcetower.unes.reactor.utils.parseZonedDateTime
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.UUID

@Component
class GradeProcessor(
    private val gradesRepo: GradeRepository
) {
    private val logger = LoggerFactory.getLogger(GradeProcessor::class.java)

    @Transactional
    suspend fun processGrades(
        clazzId: UUID,
        student: Student,
        evaluations: List<ClassEvaluation>,
        notify: Boolean
    ) {
        evaluations.forEach { evaluation ->
            evaluation.grades.forEach { grade ->
                val grades = evaluation.grades
//                val named = grades.groupBy { it.name }

//                val remapped = named.entries.map { entry ->
//                    if (entry.value.size == 1) {
//                        entry.value[0]
//                    } else {
//                        // Some disciplines still shows more than one practice.
//                        // this could be removed if we show all with the same name to the user,
//                        // but that would trigger notifications to classes we don't need.
//                        // There are 4 solutions:
//                        // - remove date changes notifications, and show everything
//                        // - remove date changes notifications, show only one grade, but date might be incorrect
//                        // - show everything
//                        // - show only one, but date might be incorrect
//                        // for now, UNES will use option 4, prioritizing earlier dates (better study for early test)
//                        entry.value.minByOrNull {
//                            when {
//                                it.value != null -> 0L
//                                it.date != null -> ZonedDateTime.parse(it.date!!).toEpochSecond()
//                                else -> Long.MAX_VALUE
//                            }
//                        }!!
//                    }
//                }

                logger.debug("Attempt to insert ${evaluation.name?.trim()} ${grade.name.trim()} ${grade.value}")
                val platformId = "${grade.id}-${student.platformId}"
                val current = gradesRepo.findByStudentClassIdAndPlatformId(clazzId, platformId)
                logger.debug("Attempt to override {} {} {}", current?.name, current?.groupingName, current?.grade)
                val date = grade.date?.trim()?.let { runCatching { ZonedDateTime.parse(it) }.getOrNull() }
                if (current == null) {
                    val notified = if (grade.hasGrade()) 3 else 1
                    val insert = Grade(
                        name = "${grade.nameShort.trim()} - ${grade.name.trim()}",
                        resumedName = grade.name.trim(),
                        studentClassId = clazzId,
                        groupingName = evaluation.name?.trim() ?: "Notas",
                        date = date,
                        grade = grade.value?.let { BigDecimal.valueOf(it) },
                        gradeRaw = grade.value?.toString(),
                        platformId = platformId,
                        notificationState = (if (notify) notified else 0).toShort()
                    ).also { it.setNew() }
                    gradesRepo.save(insert)
                } else {
                    var shouldUpdate = true
                    val score = grade.value
                    var next = current
                    if (current.hasGrade() && grade.hasGrade() && score?.toBigDecimal() != current.grade) {
                        next = current.copy(
                            notificationState = 4,
                            grade = score?.let { BigDecimal.valueOf(it) },
                            gradeRaw = score?.toString(),
                            date = date
                        )
                    } else if (!current.hasGrade() && grade.hasGrade()) {
                        next = current.copy(
                            notificationState = 3,
                            grade = score?.let { BigDecimal.valueOf(it) },
                            gradeRaw = score?.toString(),
                            date = date
                        )
                    } else if (!current.hasGrade() && !grade.hasGrade() && current.date != grade.date?.parseZonedDateTime()) {
                        next = current.copy(
                            notificationState = 2,
                            date = date
                        )
                    } else {
                        shouldUpdate = false
                        logger.debug("No changes detected between ${current.name} ${current.groupingName} and ${grade.name} ${evaluation.name.hashCode()}")
                    }

                    if (current.groupingName != evaluation.name?.trim()) {
                        shouldUpdate = true
                        next = next.copy(groupingName = evaluation.name?.trim() ?: "Notas")
                    }

                    next = next.copy(notificationState = if (notify) next.notificationState else 0)
                    if (shouldUpdate) gradesRepo.save(next)
                }
            }
        }
    }
}