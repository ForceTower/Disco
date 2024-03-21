package dev.forcetower.unes.club.domain.usecase.account

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import dev.forcetower.unes.club.domain.model.account.RegisterPasskeyFlowStart
import dev.forcetower.unes.club.domain.repository.remote.edge.AccountRepository

class ManagePasskeysUseCase internal constructor(
    private val repository: AccountRepository
) {
    @NativeCoroutines
    suspend fun registerStart(): RegisterPasskeyFlowStart {
        return repository.registerPasskeyStart()
    }

    @NativeCoroutines
    suspend fun registerFinish(flowId: String, data: String) {
        return repository.registerPasskeyFinish(flowId, data)
    }
}