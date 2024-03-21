package dev.forcetower.unes.club.domain.usecase.auth

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import dev.forcetower.unes.club.data.storage.database.ServiceAccount
import dev.forcetower.unes.club.domain.model.auth.PasskeyAssertionData
import dev.forcetower.unes.club.domain.model.auth.ServiceAuthResult
import dev.forcetower.unes.club.domain.repository.remote.edge.AccountRepository
import dev.forcetower.unes.club.domain.repository.remote.edge.AuthRepository

class ServiceAuthUseCase internal constructor(
    private val auth: AuthRepository,
    private val account: AccountRepository
) {
    @NativeCoroutines
    suspend fun handshake(): ServiceAuthResult {
        val result = auth.handshake()
        runCatching {
            if (result is ServiceAuthResult.Connected) {
                val account = account.fetchAccount()
                return ServiceAuthResult.Connected(account)
            }
        }
        return result
    }

    @NativeCoroutines
    suspend fun startAssertion(): PasskeyAssertionData {
        return auth.passkeyAssertionStart()
    }

    @NativeCoroutines
    suspend fun finishAssertion(flowId: String, credential: String): ServiceAccount {
        auth.passkeyAssertionFinish(flowId, credential)
        return account.fetchAccount()
    }

    @NativeCoroutines
    suspend fun deleteAuthAndAccount() {
        auth.deleteAuthAndAccount()
    }
}