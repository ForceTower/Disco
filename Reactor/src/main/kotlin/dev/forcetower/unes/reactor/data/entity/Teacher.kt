package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("teachers")
data class Teacher(
    @Id
    val id: UUID,
    val name: String,
    val platformId: Long
)
