package dev.forcetower.unes.club.domain.repository.remote.edge

import dev.forcetower.unes.club.data.storage.database.ServiceAccessToken
import dev.forcetower.unes.club.domain.model.auth.PasskeyAssertionData
import dev.forcetower.unes.club.domain.model.auth.ServiceAuthResult

internal interface AuthRepository {
    suspend fun connected(): Boolean
    suspend fun getAccess(): ServiceAccessToken?
    suspend fun handshake(): ServiceAuthResult
    suspend fun passkeyAssertionStart(): PasskeyAssertionData
    suspend fun passkeyAssertionFinish(flowId: String, credential: String)
    suspend fun deleteAuthAndAccount()
}