package dev.forcetower.unes.club.data.processor

import com.benasher44.uuid.uuid4
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.data.storage.database.Message
import dev.forcetower.unes.club.util.primitives.asBoolean
import dev.forcetower.unes.singer.data.model.dto.MessagesDataPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class MessagesProcessor(
    private val page: MessagesDataPage,
    private val database: GeneralDatabase,
    private val notified: Boolean = false
) {
    suspend fun execute() = withContext(Dispatchers.IO) {
        val messages = page.messages.map { it.fromMessage(notified) }
        val newMessages = database.transactionWithResult {
            messages.forEach { message ->
                prepareMessageInTransaction(message)
            }
            val result = database.messageQueries.getNewMessages().executeAsList()
            database.messageQueries.setAllNotified()
            result
        }
        newMessages
    }

    private fun prepareMessageInTransaction(message: Message) {
        val direct = database.messageQueries.getMessageByHash(message.hashMessage).executeAsOneOrNull()
        if (direct == null) {
            database.messageQueries.insertIgnore(message.content, message.platformId, message.timestamp, message.senderProfile, message.senderName, message.notified, message.discipline, message.uuid, message.codeDiscipline, message.html, message.dateString, message.processingTime, message.hashMessage, message.attachmentName, message.attachmentLink)
            return
        }

        if (message.senderName != direct.senderName) {
            database.messageQueries.updateSenderName(message.senderName, message.platformId)
        }

        if (!message.html.asBoolean() && message.content.isNotBlank()) {
            database.messageQueries.updateContent(message.content, message.platformId)
        }

        if (message.discipline != null) {
            database.messageQueries.updateDisciplineName(message.discipline, message.platformId)
        }

        if (message.codeDiscipline != null) {
            database.messageQueries.updateDisciplineCode(message.codeDiscipline, direct.platformId)
        }

        if (message.attachmentLink != null) {
            database.messageQueries.updateAttachmentLink(message.attachmentLink, message.platformId)
        }

        if (message.attachmentName != null) {
            database.messageQueries.updateAttachmentName(message.attachmentName, message.platformId)
        }

        if (message.html.asBoolean() && direct.html.asBoolean()) {
            database.messageQueries.updateDateString(message.dateString, message.platformId)
        }
    }
}

fun dev.forcetower.unes.singer.data.model.dto.Message.fromMessage(notified: Boolean): Message {
    val me = this

    val timezoneStart = me.timestamp.lastIndexOf("-").takeIf { it > 0 }
            ?: me.timestamp.lastIndexOf("+").takeIf { it > 0 }
            ?: me.timestamp.length

    val part = me.timestamp.substring(0..<timezoneStart)
    val localDateTime = LocalDateTime.parse(part)

    val timestamp = localDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    val processingTime = Clock.System.now().toEpochMilliseconds()

    return Message(
        0L,
        content = me.content.replace("\\n", "\n").replace("\\r", "\r").trim(),
        platformId = me.id,
        senderName = me.sender,
        senderProfile = me.senderType.toLong(),
        timestamp = timestamp,
        notified = if (notified) 1 else 0,
        html = 0,
        processingTime = processingTime,
        hashMessage = me.content.replace("\\n", "\n").lowercase().trim().hashCode().toLong(),
        discipline = me.discipline?.discipline,
        codeDiscipline = me.discipline?.code,
        attachmentLink = null,
        attachmentName = null,
        dateString = null,
        uuid = uuid4().toString()
    )
}