package dev.forcetower.unes.reactor.data.entity

import dev.forcetower.unes.reactor.utils.word.WordUtils
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime
import java.util.UUID

@Table("messages")
data class Message(
    @Id
    val id: UUID,
    @Column("platform_id")
    val platformId: Long,
    @Column("student_id")
    val studentId: UUID,
    val content: String,
    val timestamp: Long,
    @Column("sender_profile")
    val senderProfile: Int,
    @Column("sender_name")
    val senderName: String?,
    val notified: Boolean,
    val discipline: String?,
    @Column("code_discipline")
    val codeDiscipline: String?,
    val html: Boolean,
    @Column("date")
    val date: OffsetDateTime?,
    @Column("attachment_name")
    val attachmentName: String?,
    @Column("attachment_link")
    val attachmentLink: String?,
    @Column("created_at")
    val createdAt: OffsetDateTime
) {
    fun findTitle(): String {
        var discipline = discipline
        if (discipline == null && senderProfile == 3) discipline = "Secretaria Acadêmica"

        val text = discipline ?: senderName ?: "Nova mensagem"
        return WordUtils.toTitleCase(text)
    }
}
