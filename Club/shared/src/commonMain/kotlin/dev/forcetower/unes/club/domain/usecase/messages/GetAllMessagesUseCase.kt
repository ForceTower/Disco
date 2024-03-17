package dev.forcetower.unes.club.domain.usecase.messages

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import dev.forcetower.unes.club.data.storage.database.Message
import dev.forcetower.unes.club.domain.repository.local.MessageRepository
import kotlinx.coroutines.flow.Flow

class GetAllMessagesUseCase(
    private val repository: MessageRepository
) {
    @NativeCoroutines
    fun get(): Flow<List<Message>> {
        return repository.getAllMessages()
    }
}