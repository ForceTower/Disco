package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZonedDateTime

@Table("grades")
data class Grade(
    val name: String,
    @Column("resumed_name")
    val resumedName: String?,
    @Column("student_class_id")
    val studentClassId: UUID,
    @Column("grouping_name")
    val groupingName: String?,
    val date: ZonedDateTime?,
    val grade: BigDecimal?,
    @Column("grade_raw")
    val gradeRaw: String?,
    @Column("platform_id")
    val platformId: String,
    @Column("notification_state")
    val notificationState: Short,
    @Column("created_at")
    val createdAt: ZonedDateTime? = null,
    @Column("updated_at")
    val updatedAt: ZonedDateTime = ZonedDateTime.now(),
    @Id
    private val id: UUID? = null
) : Persistable<UUID> {
    @Transient
    private var isNew: Boolean = false
    override fun getId() = if (isNew) null else id

    override fun isNew() = isNew

    fun setNew() { this.isNew = true }

    fun hasGrade(): Boolean {
        return grade != null && grade >= BigDecimal.ZERO
    }
}