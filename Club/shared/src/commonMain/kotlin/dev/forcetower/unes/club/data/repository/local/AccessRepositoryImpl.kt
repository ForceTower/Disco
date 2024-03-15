package dev.forcetower.unes.club.data.repository.local

import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.domain.repository.local.AccessRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class AccessRepositoryImpl(
    private val database: GeneralDatabase
) : AccessRepository {
    override suspend fun insert(username: String, password: String) = withContext(Dispatchers.IO) {
        database.accessQueries.insertAccess(username, password)
    }
}