package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table(name = "student")
data class Student(
    @Id
    private val id: UUID,
    val name: String,
    @Column("platform_id")
    val platformId: Long,
    @Column("user_id")
    val userId: UUID
) : Persistable<UUID> {
    @Transient
    private var isNew: Boolean = false
    override fun getId() = if (isNew) null else id

    override fun isNew() = isNew

    fun setNew() { this.isNew = true }
}
