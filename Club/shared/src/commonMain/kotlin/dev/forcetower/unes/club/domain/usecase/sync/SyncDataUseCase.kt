package dev.forcetower.unes.club.domain.usecase.sync

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import dev.forcetower.unes.club.domain.model.sync.SyncResult
import dev.forcetower.unes.club.domain.repository.local.DisciplineRepository
import dev.forcetower.unes.club.domain.repository.local.SyncRepository

class SyncDataUseCase internal constructor(
    private val repository: SyncRepository,
    private val discipline: DisciplineRepository
) {
    @NativeCoroutines
    suspend fun execute(loadDetails: Boolean): SyncResult {
        val result = repository.sync(loadDetails)
        discipline.calculateScoreSnapshot()
        return result
    }

    @NativeCoroutines
    suspend fun fetchSync() {
        try {
            repository.fetchServerSync()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @NativeCoroutines
    fun registry() = repository.getSyncRegistry()
}