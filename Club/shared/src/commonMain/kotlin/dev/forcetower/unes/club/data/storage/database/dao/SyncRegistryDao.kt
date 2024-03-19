package dev.forcetower.unes.club.data.storage.database.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.benasher44.uuid.uuid4
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.data.storage.database.SyncRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

class SyncRegistryDao(
    private val database: GeneralDatabase
) {
    fun create(executor: String): SyncRegistry {
        return database.transactionWithResult {
            database.syncRegistryQueries.insertReplace(
                0,
                uuid4().toString(),
                Clock.System.now().toEpochMilliseconds(),
                null,
                0,
                0,
                0,
                executor,
                "",
                0
            )
            val id = database.syncRegistryQueries.lastInsertedRow().executeAsOne()
            database.syncRegistryQueries.findById(id).executeAsOne()
        }
    }

    fun finish(
        registry: SyncRegistry,
        completed: Long,
        error: Long,
        success: Long,
        message: String
    ) {
        println("Updating id: ${registry.id}")
        database.syncRegistryQueries.updateRegistry(
            completed = completed,
            error = error,
            success = success,
            message = message,
            end = Clock.System.now().toEpochMilliseconds(),
            id = registry.id
        )
    }

    fun update(element: SyncRegistry) {
        database.syncRegistryQueries.updateReplace(
            element.id,
            element.uuid,
            element.start,
            element.end,
            element.completed,
            element.success,
            element.error,
            element.executor,
            element.message,
            element.skipped,
            element.id
        )
    }

    fun selectAll(): Flow<List<SyncRegistry>> {
        return database.syncRegistryQueries.selectAll().asFlow().mapToList(Dispatchers.IO)
    }
}