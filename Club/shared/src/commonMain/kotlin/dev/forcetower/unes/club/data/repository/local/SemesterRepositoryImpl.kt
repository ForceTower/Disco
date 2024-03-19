package dev.forcetower.unes.club.data.repository.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.domain.repository.local.SemesterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SemesterRepositoryImpl(
    private val database: GeneralDatabase
) : SemesterRepository {
    override fun getCount(): Flow<Int> {
        return database.semesterQueries.count().asFlow()
            .mapToOne(Dispatchers.IO)
            .map { it.toInt() }
    }
}