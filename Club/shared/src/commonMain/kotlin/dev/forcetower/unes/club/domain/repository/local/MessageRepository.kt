package dev.forcetower.unes.club.domain.repository.local

import dev.forcetower.unes.club.data.storage.database.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getAllMessages(): Flow<List<Message>>
    fun getLastMessage(): Flow<Message?>
    suspend fun getPendingNotifications(markNotified: Boolean): List<Message>
    suspend fun markAllNotified()
}