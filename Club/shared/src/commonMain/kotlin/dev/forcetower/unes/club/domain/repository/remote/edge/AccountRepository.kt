package dev.forcetower.unes.club.domain.repository.remote.edge

import dev.forcetower.unes.club.data.storage.database.ServiceAccount
import dev.forcetower.unes.club.domain.model.account.RegisterPasskeyFlowStart
import dev.forcetower.unes.club.domain.model.auth.ServiceLinkEmailCompleteResult
import kotlinx.coroutines.flow.Flow

internal interface AccountRepository {
    fun getAccount(): Flow<ServiceAccount?>
    suspend fun fetchAccount(): ServiceAccount
    suspend fun fetchAccountIfConnected(): ServiceAccount?
    suspend fun registerEmail(email: String): String
    suspend fun completeEmailRegister(code: String, security: String): ServiceLinkEmailCompleteResult
    suspend fun registerPasskeyStart(): RegisterPasskeyFlowStart
    suspend fun registerPasskeyFinish(flowId: String, data: String)
    suspend fun registerMessagingTokenIfNeeded(token: String)
}