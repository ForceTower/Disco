package dev.forcetower.unes.reactor.service.snowpiercer

import dev.forcetower.breaker.Orchestra
import dev.forcetower.breaker.model.MessagesDataPage
import dev.forcetower.unes.reactor.data.entity.Student
import dev.forcetower.unes.reactor.data.repository.MessageRepository
import dev.forcetower.unes.reactor.data.repository.SemesterRepository
import dev.forcetower.unes.reactor.processor.DisciplineProcessor
import dev.forcetower.unes.reactor.processor.MessagesProcessor
import dev.forcetower.unes.reactor.processor.SemestersProcessor
import dev.forcetower.unes.reactor.service.notification.UserNotificationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SnowpiercerUpdateService(
    private val orchestra: Orchestra,
    private val userNotification: UserNotificationService,
    private val semesterRepo: SemesterRepository,
    private val messagesProc: MessagesProcessor,
    private val messagesRepo: MessageRepository,
    private val semesterProc: SemestersProcessor,
    private val disciplineProc: DisciplineProcessor
) {
    private val logger = LoggerFactory.getLogger(SnowpiercerUpdateService::class.java)

    suspend fun update(student: Student, notify: Boolean) {
        val platformId = student.platformId
        val studentId = student.id!!

        val messages = orchestra.messages(platformId).success()?.value
        messages?.let {
            messagesProc.execute(student, it.messages, notify)
        }

        val newMessages = messagesRepo.newMessages(studentId)
        userNotification.notifyMessages(newMessages, student)
        messagesRepo.markMessagesNotified(studentId)

        val semesters = orchestra.semesters(platformId).success()?.value ?: run {
            logger.warn("Failed to load semester for student {}", student.id)
            return
        }

        semesterProc.execute(semesters)
        val semester = semesters.maxByOrNull { it.id } ?: return
        val localSemester = semesterRepo.findByPlatformId(semester.id) ?: run {
            logger.warn("Local semester not found after insertion.")
            return
        }

        val data = orchestra.grades(platformId, semester.id).success()?.value ?: return
        disciplineProc.execute(student, localSemester, data, notify)
    }
}