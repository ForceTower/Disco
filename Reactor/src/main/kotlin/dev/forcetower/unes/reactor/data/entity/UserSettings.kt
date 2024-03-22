package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("user_settings")
data class UserSettings(
    @Id
    val id: UUID,
    val userId: UUID,
    val gradeSpoiler: Short
)
