package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.UserSettings
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserSettingsRepository : CoroutineCrudRepository<UserSettings, UUID> {
    @Query("INSERT INTO user_settings(user_id) VALUES (:userId) ON CONFLICT DO NOTHING")
    suspend fun createForUser(userId: UUID)

    @Query("SELECT * FROM user_settings WHERE user_id = :userId")
    suspend fun findByUserId(userId: UUID): UserSettings?
    @Query("UPDATE user_settings SET initial_sync_completed = :synced WHERE user_id = :userId")
    suspend fun updateInitialSyncForUser(userId: UUID, synced: Boolean)
}