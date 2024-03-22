package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("disciplines")
data class Discipline(
    @Id
    val id: UUID,
    val code: String,
    val name: String,
    val program: String?,
    val credits: Int,
    val departmentId: UUID,
    val fullCode: String?
)
