package dev.forcetower.unes.club.data.repository.remote.edge

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import dev.forcetower.unes.club.data.service.client.AccountService
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.data.storage.database.ServiceAccount
import dev.forcetower.unes.club.domain.repository.remote.edge.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

internal class AccountRepositoryImpl(
    private val database: GeneralDatabase,
    private val service: AccountService
) : AccountRepository {
    override fun getAccount(): Flow<ServiceAccount?> {
        return database.serviceAccountQueries.selectMe().asFlow().mapToOneOrNull(Dispatchers.IO)
    }

    override suspend fun fetchAccount(): ServiceAccount {
        val me = service.me()
        val next = ServiceAccount(me.id, me.name, me.email, me.imageUrl, 1)
        database.serviceAccountQueries.insertReplace(next)
        return next
    }
}