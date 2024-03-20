package dev.forcetower.unes.club.domain.usecase.account

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import dev.forcetower.unes.club.data.storage.database.ServiceAccount
import dev.forcetower.unes.club.domain.repository.remote.edge.AccountRepository
import kotlinx.coroutines.flow.Flow

class GetAccountUseCase internal constructor(
    private val repository: AccountRepository
) {
    @NativeCoroutines
    suspend fun fetchAccount(): ServiceAccount {
        return repository.fetchAccount()
    }

    @NativeCoroutines
    fun getAccount(): Flow<ServiceAccount?> {
        return repository.getAccount()
    }
}