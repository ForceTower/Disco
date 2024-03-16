package dev.forcetower.unes.club.data.storage.database.dao

import dev.forcetower.unes.club.data.storage.database.ClassItem
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase

class ClassItemDao(
    private val database: GeneralDatabase
) {
    fun getItemByIdentifiers(groupId: Long, ordinal: Int): ClassItem? {
        return database.classItemQueries.getItemByIdentifiers(groupId, ordinal.toLong()).executeAsOneOrNull()
    }

    fun update(item: ClassItem) {
        database.classItemQueries.updateReplace(
            item.id,
            item.groupId,
            item.number,
            item.situation,
            item.subject,
            item.date,
            item.numberOfMaterials,
            item.materialLinks,
            item.isNew,
            item.id
        )
    }

    fun insert(item: ClassItem): Long {
        database.classItemQueries.insertReplace(
            item.id,
            item.groupId,
            item.number,
            item.situation,
            item.subject,
            item.date,
            item.numberOfMaterials,
            item.materialLinks,
            item.isNew
        )
        return database.classItemQueries.lastInsertedRow().executeAsOne()
    }
}