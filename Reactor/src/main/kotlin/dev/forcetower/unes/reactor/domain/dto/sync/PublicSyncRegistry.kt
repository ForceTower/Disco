package dev.forcetower.unes.reactor.domain.dto.sync

import dev.forcetower.unes.reactor.data.entity.SyncRegistry
import java.time.ZonedDateTime
import java.util.UUID

data class PublicSyncRegistry(
    val id: UUID,
    val executor: String,
    val completed: Boolean,
    val success: Boolean?,
    val error: Int?,
    val message: String?,
    val startAt: ZonedDateTime,
    val finishedAt: ZonedDateTime?
) {

    companion object {
        fun createFrom(registry: SyncRegistry): PublicSyncRegistry {
            return PublicSyncRegistry(
                registry.id,
                registry.executor,
                registry.completed,
                registry.success,
                registry.error,
                registry.message,
                registry.startAt,
                registry.finishedAt
            )
        }
    }
}
