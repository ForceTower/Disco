package dev.forcetower.unes.reactor.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.ZonedDateTime
import java.util.UUID

@Table("sync_registry")
data class SyncRegistry(
    @Id
    val id: UUID,
    val studentId: UUID,
    val executor: String,
    val completed: Boolean,
    val success: Boolean?,
    val error: Int?,
    val message: String?,
    val startAt: ZonedDateTime,
    val finishedAt: ZonedDateTime?
)
