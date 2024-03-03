package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("user_course")
data class UserCourse(
    @Id
    val id: UUID,
    @Column("user_id")
    val userId: UUID,
    @Column("course_id")
    val courseId: UUID
)