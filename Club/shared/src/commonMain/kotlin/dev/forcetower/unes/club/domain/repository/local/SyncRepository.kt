package dev.forcetower.unes.club.domain.repository.local

import dev.forcetower.unes.club.data.storage.database.SyncRegistry
import dev.forcetower.unes.club.domain.model.sync.SyncResult
import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    suspend fun sync(loadDetails: Boolean): SyncResult
    fun getSyncRegistry(): Flow<List<SyncRegistry>>
    suspend fun fetchServerSync()
}