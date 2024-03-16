package dev.forcetower.unes.club.data.storage.database.dao

import dev.forcetower.unes.club.data.storage.database.Class
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase

class ClassDao(
    private val database: GeneralDatabase
) {
    fun insertNewWays(clazz: Class): Long {
        val current = getClassDirectlyNew(clazz.semesterId, clazz.disciplineId)
        return if (current != null) {
            update(current.copy(finalScore = clazz.finalScore, missedClasses = clazz.missedClasses))
            current.id
        } else {
            insert(clazz)
        }
    }

    private fun insert(clazz: Class): Long {
        return database.transactionWithResult {
            database.classQueries.insertIgnore(
                clazz.disciplineId,
                clazz.semesterId,
                clazz.status,
                clazz.finalScore,
                clazz.partialScore,
                clazz.missedClasses,
                clazz.lastClass,
                clazz.nextClass,
                clazz.scheduleOnly
            )
            database.classQueries.lastInsertedRow().executeAsOne()
        }
    }

    private fun update(clazz: Class) {
        database.classQueries.update(
            clazz.id,
            clazz.disciplineId,
            clazz.semesterId,
            clazz.status,
            clazz.finalScore,
            clazz.partialScore,
            clazz.missedClasses,
            clazz.lastClass,
            clazz.nextClass,
            clazz.scheduleOnly,
            clazz.id
        )
    }

    private fun getClassDirectlyNew(semesterId: Long, disciplineId: Long): Class? {
        return database.classQueries.getClassNew(semesterId, disciplineId).executeAsOneOrNull()
    }
}