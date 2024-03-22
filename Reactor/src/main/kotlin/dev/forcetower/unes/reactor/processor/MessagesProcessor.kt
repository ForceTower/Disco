package dev.forcetower.unes.reactor.processor

import dev.forcetower.unes.reactor.data.entity.Student
import dev.forcetower.unes.reactor.data.repository.MessageRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Component
class MessagesProcessor(
    private val repository: MessageRepository
) {
    @Transactional
    suspend fun execute(
        student: Student,
        unprepared: List<dev.forcetower.breaker.model.Message>,
        notify: Boolean
    ) {
        val studentId = student.id!!
        unprepared.forEach { message ->
            prepareMessage(studentId, message, notify)
        }
    }

    private suspend fun prepareMessage(
        studentId: UUID,
        message: dev.forcetower.breaker.model.Message,
        notify: Boolean
    ) {
        val receivedAt = OffsetDateTime.parse(message.timestamp)
        val timestamp = receivedAt.toInstant().toEpochMilli()

        val direct = repository.findByPlatformId(message.id, studentId).awaitSingleOrNull()
        if (direct == null) {
            repository.insertMessage(
                message.id,
                studentId,
                message.content.replace("\\n", "\n").replace("\\r", "\r"),
                timestamp,
                message.senderType,
                message.sender,
                !notify,
                message.discipline?.discipline,
                message.discipline?.code,
                receivedAt
            )
            return
        }

        if (message.sender != direct.senderName) {
            repository.updateSenderName(message.sender, message.id, studentId)
        }

        if (message.content.isNotBlank()) {
            repository.updateContent(
                message.content.replace("\\n", "\n").replace("\\r", "\r"),
                message.id,
                studentId
            )
        }

        if (message.discipline != null) {
            repository.updateDisciplineName(message.discipline?.discipline, message.id, studentId)
        }

        if (message.discipline?.code != null) {
            repository.updateDisciplineCode(message.discipline?.code, direct.platformId, studentId)
        }
    }
}