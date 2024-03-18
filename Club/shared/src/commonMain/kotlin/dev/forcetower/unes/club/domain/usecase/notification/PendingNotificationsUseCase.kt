package dev.forcetower.unes.club.domain.usecase.notification

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import dev.forcetower.unes.club.data.storage.database.Message
import dev.forcetower.unes.club.domain.model.grade.GradeData
import dev.forcetower.unes.club.domain.repository.local.GradeRepository
import dev.forcetower.unes.club.domain.repository.local.MessageRepository

class PendingNotificationsUseCase(
    private val messages: MessageRepository,
    private val grades: GradeRepository
) {
    @NativeCoroutines
    suspend fun messages(markNotified: Boolean): List<Message> {
        return messages.getPendingNotifications(markNotified)
    }

    @NativeCoroutines
    suspend fun grades(markNotified: Boolean): List<GradeData> {
        return grades.getPendingNotifications(markNotified)
    }
}