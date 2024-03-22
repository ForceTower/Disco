package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZonedDateTime

@Table("student_classes")
data class StudentClass(
    val id: UUID,
    @Column("student_id")
    val studentId: UUID,
    @Column("class_id")
    val classId: UUID,
    @Column("final_grade")
    val finalGrade: BigDecimal? = null,
    @Column("final_grade_raw")
    val finalGradeRaw: String? = null,
    @Column("partial_grade")
    val partialGrade: BigDecimal? = null,
    @Column("partial_grade_raw")
    val partialGradeRaw: String? = null,
    @Column("created_at")
    val createdAt: ZonedDateTime? = null
)