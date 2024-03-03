package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("course")
data class Course(
    val id: UUID,
    val name: String,
    @Column("image_url")
    val imageUrl: String?
)
