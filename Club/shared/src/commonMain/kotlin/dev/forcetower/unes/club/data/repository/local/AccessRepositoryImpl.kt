package dev.forcetower.unes.club.data.repository.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import dev.forcetower.unes.club.data.storage.database.Access
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.data.storage.database.Profile
import dev.forcetower.unes.club.domain.repository.local.AccessRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class AccessRepositoryImpl(
    private val database: GeneralDatabase
) : AccessRepository {
    override suspend fun insert(username: String, password: String) = withContext(Dispatchers.IO) {
        database.accessQueries.insertAccess(username, password)
    }

    override suspend fun requireCurrentAccess(): Access? = withContext(Dispatchers.IO) {
        database.accessQueries.selectAccess().executeAsOneOrNull()
    }

    override fun access(): Flow<Access?> {
        return database.accessQueries.selectAccess().asFlow().mapToOneOrNull(Dispatchers.IO)
    }

    override fun currentProfile(): Flow<Profile?> {
        return database.profileQueries.selectMe().asFlow().mapToOneOrNull(Dispatchers.IO)
    }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        database.transaction {
            database.accessQueries.deleteAll()
            database.profileQueries.deleteAll()
            database.messageQueries.deleteAll()
            database.classQueries.deleteAll()
            database.gradeQueries.deleteAll()
            database.semesterQueries.deleteAll()
            database.disciplineQueries.deleteAll()
        }
    }
}