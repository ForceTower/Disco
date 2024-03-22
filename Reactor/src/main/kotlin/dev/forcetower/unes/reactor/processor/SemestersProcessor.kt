package dev.forcetower.unes.reactor.processor

import dev.forcetower.unes.reactor.data.entity.Semester
import dev.forcetower.unes.reactor.data.repository.SemesterRepository
import kotlinx.coroutines.flow.toCollection
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

@Component
class SemestersProcessor(
    private val repository: SemesterRepository
) {
    suspend fun execute(semesters: List<dev.forcetower.breaker.model.Semester>) {
        semesters.forEach {
            repository.insertIgnore(
                it.code.trim(),
                it.description.trim(),
                it.id,
                runCatching { ZonedDateTime.parse(it.start) }.getOrNull(),
                runCatching { ZonedDateTime.parse(it.end) }.getOrNull(),
                runCatching { ZonedDateTime.parse(it.start) }.getOrNull(),
                runCatching { ZonedDateTime.parse(it.end) }.getOrNull(),
            )
        }
    }
}