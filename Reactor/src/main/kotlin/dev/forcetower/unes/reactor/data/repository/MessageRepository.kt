package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.Message
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.util.UUID

@Repository
interface MessageRepository : CoroutineCrudRepository<Message, UUID> {
    @Query("SELECT * FROM messages WHERE student_id = :studentId")
    fun findMessagesByStudentId(studentId: UUID): Mono<List<Message>>

    @Query("SELECT * FROM messages WHERE student_id = :studentId AND notified = false")
    fun findNotificationPendingMessages(studentId: UUID): Mono<List<Message>>

    @Query("SELECT * FROM messages WHERE platform_id = :platformId AND student_id = :studentId")
    fun findByPlatformId(platformId: Long, studentId: UUID): Mono<Message?>

    @Query("UPDATE messages SET sender_name = :senderName WHERE platform_id = :platformId AND student_id = :studentId")
    suspend fun updateSenderName(senderName: String, platformId: Long, studentId: UUID)

    @Query("UPDATE messages SET content = :content WHERE platform_id = :platformId AND student_id = :studentId")
    suspend fun updateContent(content: String, platformId: Long, studentId: UUID)

    @Query("UPDATE messages SET discipline = :discipline WHERE platform_id = :platformId AND student_id = :studentId")
    suspend fun updateDisciplineName(discipline: String?, platformId: Long, studentId: UUID)

    @Query("UPDATE messages SET code_discipline = :codeDiscipline WHERE platform_id = :platformId AND student_id = :studentId")
    suspend fun updateDisciplineCode(codeDiscipline: String?, platformId: Long, studentId: UUID)

    @Query("UPDATE messages SET attachment_link = :attachmentLink WHERE platform_id = :platformId AND student_id = :studentId")
    suspend fun updateAttachmentLink(attachmentLink: String, platformId: Long, studentId: UUID)

    @Query("UPDATE messages SET attachment_name = :attachmentName WHERE platform_id = :platformId AND student_id = :studentId")
    suspend fun updateAttachmentName(attachmentName: String, platformId: Long, studentId: UUID)

    @Query("INSERT INTO messages(platform_id, student_id, content, timestamp, sender_profile, sender_name, notified, discipline, code_discipline, date, attachment_name, attachment_link) values (:platformId, :studentId, :content, :timestamp, :senderProfile, :senderName, :notified, :discipline, :disciplineCode, :date, null, null)")
    suspend fun insertMessage(
        platformId: Long,
        studentId: UUID,
        content: String,
        timestamp: Long,
        senderProfile: Int,
        senderName: String,
        notified: Boolean,
        discipline: String?,
        disciplineCode: String?,
        date: OffsetDateTime
    ): Message?

    @Query("SELECT * FROM messages WHERE student_id = :studentId AND notified = false")
    suspend fun newMessages(studentId: UUID): List<Message>

    @Query("UPDATE messages SET notified = true WHERE student_id = :studentId")
    suspend fun markMessagesNotified(studentId: UUID)
}