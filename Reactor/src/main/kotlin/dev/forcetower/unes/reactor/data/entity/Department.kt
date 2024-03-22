package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("departments")
data class Department(
    val name: String,
    val code: String,
    val phone: String?,
    val site: String?,
    val email: String?,
    @Id
    private val id: UUID? = null
)  : Persistable<UUID> {
    @Transient
    private var isNew: Boolean = false
    override fun getId() = if (isNew) null else id

    override fun isNew() = isNew

    fun setNew() { this.isNew = true }
}