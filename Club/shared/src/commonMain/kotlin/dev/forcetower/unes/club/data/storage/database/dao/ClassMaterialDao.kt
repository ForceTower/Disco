package dev.forcetower.unes.club.data.storage.database.dao

import dev.forcetower.unes.club.data.storage.database.ClassMaterial
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase

class ClassMaterialDao(
    private val database: GeneralDatabase
) {
    fun getMaterialsByIdentifiers(name: String, link: String, groupId: Long): ClassMaterial? {
        return database.classMaterialQueries.getMaterialsByIdentifiers(groupId, name, link).executeAsOneOrNull()
    }

    fun update(item: ClassMaterial) {
        database.classMaterialQueries.updateReplace(
            item.id,
            item.groupId,
            item.classItemId,
            item.name,
            item.link,
            item.isNew,
            item.uuid,
            item.notified,
            item.id
        )
    }

    fun insert(item: ClassMaterial) {
        database.classMaterialQueries.insertIgnore(
            item.id,
            item.groupId,
            item.classItemId,
            item.name,
            item.link,
            item.isNew,
            item.uuid,
            item.notified,
        )
    }
}