package dev.forcetower.unes.club.data.storage.database.dao

import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.data.storage.database.Teacher

class TeacherDao(
    private val database: GeneralDatabase
) {
    fun insertOrUpdate(teacher: Teacher): Long {
        return database.transactionWithResult {
            database.teacherQueries.insertReplace(
                teacher.id,
                teacher.name,
                teacher.email,
                teacher.platformId,
                teacher.department
            )
            database.teacherQueries.lastInsertedRow().executeAsOne()
        }
    }
}