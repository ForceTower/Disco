package dev.forcetower.unes.club.domain.repository.local

import dev.forcetower.unes.club.domain.model.sync.SyncResult

interface SyncRepository {
    suspend fun sync(): SyncResult
}