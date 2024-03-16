package dev.forcetower.unes.club.data.processor

import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.data.storage.database.Semester
import dev.forcetower.unes.club.util.date.parseZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.toInstant

class SemestersProcessor(
    private val semesters: List<dev.forcetower.unes.singer.data.model.dto.Semester>,
    private val database: GeneralDatabase
) {
    suspend fun execute() = withContext(Dispatchers.IO) {
        val mapped = semesters.map {
            val (start, startOff) = parseZonedDateTime(it.start)
            val (end, endOff) = parseZonedDateTime(it.end)
            Semester(
                id = 0L,
                platformId = it.id,
                name = it.code.trim(),
                codename = it.description.trim(),
                start = start.toInstant(startOff).toEpochMilliseconds(),
                end = end.toInstant(endOff).toEpochMilliseconds(),
                startClass = start.toInstant(startOff).toEpochMilliseconds(),
                endClass = end.toInstant(endOff).toEpochMilliseconds()
            )
        }
        database.transaction {
            mapped.forEach { semester ->
                database.semesterQueries.insertIgnoring(
                    semester.platformId,
                    semester.name,
                    semester.codename,
                    semester.start,
                    semester.end,
                    semester.startClass,
                    semester.endClass
                )
            }
        }
    }
}