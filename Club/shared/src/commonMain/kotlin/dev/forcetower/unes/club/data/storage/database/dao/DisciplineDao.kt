package dev.forcetower.unes.club.data.storage.database.dao

import dev.forcetower.unes.club.data.storage.database.Discipline
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase

class DisciplineDao(
    private val database: GeneralDatabase
) {
    fun insertOrUpdate(value: Discipline): Long {
        val current = getDisciplineByCodeDirect(value.code)
        return if (current != null) {
            if (current != value) {
                update(value.copy(id = current.id))
            }
            current.id
        } else {
            insert(value)
        }
    }

    private fun insert(value: Discipline): Long {
        return database.transactionWithResult {
            database.disciplineQueries.insertIgnore(
                value.name,
                value.code,
                value.credits,
                value.department,
                value.resume,
                value.shortText
            )
            database.disciplineQueries.lastInsertedRow().executeAsOne()
        }
    }

    private fun update(value: Discipline) {
        database.disciplineQueries.updateIgnore(
            value.id,
            value.name,
            value.code,
            value.credits,
            value.department,
            value.resume,
            value.shortText,
            value.id
        )
    }

    private fun getDisciplineByCodeDirect(code: String): Discipline? {
        return database.disciplineQueries.getDisciplineByCode(code).executeAsOneOrNull()
    }
}