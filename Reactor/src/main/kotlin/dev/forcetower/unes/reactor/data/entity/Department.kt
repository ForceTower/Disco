package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("departments")
data class Department(
    @Id
    val id: UUID,
    val name: String,
    val code: String,
    val phone: String?,
    val site: String?,
    val email: String?
)