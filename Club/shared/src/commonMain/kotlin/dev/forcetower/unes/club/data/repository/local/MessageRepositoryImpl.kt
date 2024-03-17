package dev.forcetower.unes.club.data.repository.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.data.storage.database.Message
import dev.forcetower.unes.club.domain.repository.local.MessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

internal class MessageRepositoryImpl(
    private val database: GeneralDatabase
) : MessageRepository {
    override fun getAllMessages(): Flow<List<Message>> {
        return database.messageQueries.getAllMessages().asFlow().mapToList(Dispatchers.IO)
    }

    override fun getLastMessage(): Flow<Message?> {
        return database.messageQueries.getLastMessage().asFlow().mapToOneOrNull(Dispatchers.IO)
    }
}