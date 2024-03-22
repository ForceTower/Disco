package dev.forcetower.unes.reactor.service.snowpiercer

import dev.forcetower.breaker.Orchestra
import dev.forcetower.breaker.model.MessagesDataPage
import dev.forcetower.unes.reactor.data.entity.Student
import dev.forcetower.unes.reactor.data.repository.MessageRepository
import dev.forcetower.unes.reactor.processor.MessagesProcessor
import dev.forcetower.unes.reactor.service.notification.UserNotificationService
import org.springframework.stereotype.Service

@Service
class SnowpiercerUpdateService(
    private val orchestra: Orchestra,
    private val userNotification: UserNotificationService,
    private val messagesProc: MessagesProcessor,
    private val messagesRepo: MessageRepository
) {
    suspend fun update(student: Student, notify: Boolean) {
        val platformId = student.platformId
        val studentId = student.id!!

        val messages = orchestra.messages(platformId).success()?.value
        messages?.let {
            messagesProc.execute(student, it.messages, notify)
        }

        val newMessages = messagesRepo.newMessages(studentId)
        userNotification.notifyMessages(newMessages, student)
//        messagesRepo.markMessagesNotified(studentId)

        val semesters = orchestra.semesters(platformId).success()?.value
        val semester = semesters?.maxByOrNull { it.id } ?: return

        val data = orchestra.grades(platformId, semester.id).success()?.value ?: return
        data.forEach {

        }
    }
}