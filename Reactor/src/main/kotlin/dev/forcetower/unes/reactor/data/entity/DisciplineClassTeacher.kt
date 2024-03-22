package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("class_teacher")
data class DisciplineClassTeacher(
    @Id
    val id: UUID,
    val classId: UUID,
    val teacherId: UUID
)
