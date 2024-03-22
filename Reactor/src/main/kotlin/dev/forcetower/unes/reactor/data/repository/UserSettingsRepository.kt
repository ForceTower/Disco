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

    suspend fun findByUserId(userId: UUID): UserSettings?
}