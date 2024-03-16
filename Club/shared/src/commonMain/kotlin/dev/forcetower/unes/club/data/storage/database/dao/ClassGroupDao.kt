package dev.forcetower.unes.club.data.storage.database.dao

import dev.forcetower.unes.club.data.storage.database.ClassGroup
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase

class ClassGroupDao(
    private val database: GeneralDatabase
) {
    fun insertNewWay(group: ClassGroup): Long {
        val groups = selectGroupsFromClassDirect(group.classId)
        when {
            // just insert, you are brand new
            groups.isEmpty() -> {
                return insert(group)
            }
            groups.size == 1 -> {
                val current = groups.first()
                // are we merging?
                return if (current.group.equals("unique", ignoreCase = true) || current.group.equals(group.group, ignoreCase = true)) {
                    // yes, we are
                    update(group.copy(id = current.id, ignored = current.ignored))
                    current.id
                } else {
                    // no, we are not, you new
                    insert(group)
                }
            }
            // there are plenty of groups here... find the one that fits
            else -> {
                val current = groups.firstOrNull { it.group == "unique" || it.group.equals(group.group, ignoreCase = true) }
                return if (current != null) {
                    // merge the fitter
                    update(group.copy(id = current.id, ignored = current.ignored))
                    current.id
                } else {
                    // no one fits
                    insert(group)
                }
            }
        }
    }

    private fun selectGroupsFromClassDirect(classId: Long): List<ClassGroup> {
        return database.classGroupQueries.selectGroupsFromClass(classId).executeAsList()
    }

    private fun update(group: ClassGroup) {
        return database.classGroupQueries.update(
            group.id,
            group.classId,
            group.group,
            group.teacher,
            group.credits,
            group.draft,
            group.ignored,
            group.teacherId,
            group.platformId,
            group.teacherEmail,
            group.id
        )
    }

    private fun insert(group: ClassGroup): Long {
        return database.transactionWithResult {
            database.classGroupQueries.insertIgnore(
                group.classId,
                group.group,
                group.teacher,
                group.credits,
                group.draft,
                group.ignored,
                group.teacherId,
                group.platformId,
                group.teacherEmail,
            )
            database.classGroupQueries.lastInsertedRow().executeAsOne()
        }
    }
}