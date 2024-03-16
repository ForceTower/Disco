package dev.forcetower.unes.club.data.storage.database.dao

import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.data.storage.database.Semester
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SemesterDao(
    private val database: GeneralDatabase
) {
    fun selectAll(): List<Semester> {
        return database.semesterQueries.selectAll().executeAsList()
    }
}