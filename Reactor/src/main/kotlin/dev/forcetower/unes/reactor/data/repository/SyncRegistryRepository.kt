package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.SyncRegistry
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime
import java.util.UUID

@Repository
interface SyncRegistryRepository : CoroutineCrudRepository<SyncRegistry, UUID> {
    @Query("INSERT INTO sync_registry(student_id, executor, start_at) VALUES (:studentId, :executor, :startAt) RETURNING id")
    suspend fun createRegistry(studentId: UUID, executor: String, startAt: ZonedDateTime = ZonedDateTime.now()): UUID

    @Query("UPDATE sync_registry SET finished_at = :finishedAt, completed = :completed, success = :success, error = :error, message = :message WHERE id = :registryId")
    suspend fun updateRegistry(
        registryId: UUID,
        success: Boolean,
        error: Int,
        message: String?,
        completed: Boolean = true,
        finishedAt: ZonedDateTime = ZonedDateTime.now()
    )

    @Query("SELECT * FROM sync_registry WHERE student_id = :studentId ORDER BY start_at DESC LIMIT 10")
    suspend fun findAllFromStudent(studentId: UUID): List<SyncRegistry>
}