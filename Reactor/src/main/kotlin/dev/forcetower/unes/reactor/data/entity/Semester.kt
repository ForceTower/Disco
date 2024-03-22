package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.ZonedDateTime
import java.util.UUID

@Table("semesters")
data class Semester(
    val name: String,
    val codename: String,
    @Column("platform_id")
    val platformId: Long,
    val start: ZonedDateTime?,
    val finish: ZonedDateTime?,
    val startClass: ZonedDateTime?,
    val endClass: ZonedDateTime?,
    @Id
    private var id: UUID? = null
) : Persistable<UUID> {
    @Transient
    private var isNew: Boolean = false
    override fun getId() = if (isNew) null else id

    override fun isNew() = isNew

    fun setNew() { this.isNew = true }
}
