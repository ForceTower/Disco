package dev.forcetower.unes.club.data.storage.database.dao

import dev.forcetower.unes.club.data.storage.database.ClassAbsence
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase

class ClassAbsenceDao(
    private val database: GeneralDatabase
) {
    fun insert(items: List<ClassAbsence>) {
        database.transaction {
            items.forEach { item ->
                database.classAbsenceQueries.insertIgnore(
                    item.id,
                    item.classId,
                    item.profileId,
                    item.sequence,
                    item.description,
                    item.date,
                    item.grouping,
                    item.uuid,
                    item.notified
                )
            }
        }
    }
}