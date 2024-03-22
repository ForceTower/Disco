package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("disciplines")
data class Discipline(
    val code: String,
    val name: String,
    val program: String?,
    val credits: Int,
    val departmentId: UUID?,
    val fullCode: String?,
    @Id
    private val id: UUID? = UUID.randomUUID()
)  : Persistable<UUID> {
    @Transient
    private var isNew: Boolean = false
    override fun getId() = if (isNew) null else id

    override fun isNew() = isNew

    fun setNew() { this.isNew = true }
}
