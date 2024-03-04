package dev.forcetower.unes.reactor.service.snowpiercer

import dev.forcetower.breaker.Orchestra
import dev.forcetower.breaker.model.MessagesDataPage
import dev.forcetower.unes.reactor.data.entity.Student
import org.springframework.stereotype.Service

@Service
class SnowpiercerUpdateService(
    private val orchestra: Orchestra
) {
    suspend fun update(student: Student, notify: Boolean) {
        val platformId = student.platformId

        val messages = orchestra.messages(platformId).success()?.value
        messages?.let {
            processMessages(student, messages, notify)
        }

        val semesters = orchestra.semesters(platformId).success()?.value
        val semester = semesters?.maxByOrNull { it.id } ?: return

        val data = orchestra.grades(platformId, semester.id).success()?.value ?: return
        data.forEach {

        }
    }

    private suspend fun processMessages(student: Student, page: MessagesDataPage, notify: Boolean) {
        val messages = page.messages
        messages.forEach { message ->
            message.id
        }
    }
}