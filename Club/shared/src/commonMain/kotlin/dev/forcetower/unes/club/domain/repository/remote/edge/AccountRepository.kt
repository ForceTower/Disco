package dev.forcetower.unes.club.domain.repository.remote.edge

import dev.forcetower.unes.club.data.storage.database.ServiceAccount
import kotlinx.coroutines.flow.Flow

internal interface AccountRepository {
    fun getAccount(): Flow<ServiceAccount?>
    suspend fun fetchAccount(): ServiceAccount
}