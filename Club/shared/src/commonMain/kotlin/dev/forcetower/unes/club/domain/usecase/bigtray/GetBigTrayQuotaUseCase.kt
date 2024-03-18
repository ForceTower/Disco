package dev.forcetower.unes.club.domain.usecase.bigtray

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import dev.forcetower.unes.club.domain.model.bigtray.BigTrayData
import dev.forcetower.unes.club.domain.repository.remote.uefs.BigTrayRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

class GetBigTrayQuotaUseCase internal constructor(
    private val repository: BigTrayRepository
) {
    @NativeCoroutines
    fun quota(delayMs: Long = 10000): Flow<BigTrayData> = flow {
        while (currentCoroutineContext().isActive) {
            emit(repository.getQuota())
            delay(delayMs)
        }
    }
}