package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("user_messaging_token")
data class MessagingToken(
    val token: String,
    @Column("user_id")
    val userId: UUID,
    @Id
    private val id: UUID = UUID.randomUUID()
): Persistable<UUID> {
    @Transient
    private var isNew = false
    override fun getId() = if (isNew) null else id
    override fun isNew() = isNew
    fun setNew() { this.isNew = true }
}
