package dev.forcetower.unes.club.data.repository.local

import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.domain.model.grade.GradeData
import dev.forcetower.unes.club.domain.repository.local.GradeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class GradeRepositoryImpl(
    private val database: GeneralDatabase
) : GradeRepository {
    override suspend fun getPendingNotifications(markNotified: Boolean): List<GradeData> = withContext(Dispatchers.IO) {
        val grades = database.gradeQueries.getPendingNotifications().executeAsList()

        val result = grades.mapNotNull {
            val clazz = database.classQueries.findById(it.classId).executeAsOneOrNull() ?: return@mapNotNull null
            val discipline = database.disciplineQueries.findById(clazz.disciplineId).executeAsOneOrNull() ?: return@mapNotNull null
            GradeData(it, discipline)
        }

        if (markNotified) {
            val elements = result.map { it.ref.id }
            database.gradeQueries.markSelectedNotified(elements)
        }
        result
    }

    override suspend fun markAllNotified() = withContext(Dispatchers.IO) {
        database.gradeQueries.markAllNotified()
    }
}