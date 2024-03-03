package dev.forcetower.unes.reactor.domain.dto.student

import java.util.UUID

data class PublicStudent(
    val id: UUID,
    val name: String,
    val courseId: UUID?,
    val courseName: String?
)