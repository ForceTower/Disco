package dev.forcetower.unes.club.data.storage.database

import app.cash.sqldelight.TransactionWithReturn
import app.cash.sqldelight.TransactionWithoutReturn
import dev.forcetower.unes.club.data.storage.database.dao.ClassAbsenceDao
import dev.forcetower.unes.club.data.storage.database.dao.ClassDao
import dev.forcetower.unes.club.data.storage.database.dao.ClassGroupDao
import dev.forcetower.unes.club.data.storage.database.dao.ClassItemDao
import dev.forcetower.unes.club.data.storage.database.dao.ClassLocationDao
import dev.forcetower.unes.club.data.storage.database.dao.ClassMaterialDao
import dev.forcetower.unes.club.data.storage.database.dao.DisciplineDao
import dev.forcetower.unes.club.data.storage.database.dao.GradeDao
import dev.forcetower.unes.club.data.storage.database.dao.ProfileDao
import dev.forcetower.unes.club.data.storage.database.dao.SemesterDao
import dev.forcetower.unes.club.data.storage.database.dao.TeacherDao

class GeneralDB(
    private val database: GeneralDatabase
) {
    val profileDao = ProfileDao(database)
    val teacherDao = TeacherDao(database)
    val semesterDao = SemesterDao(database)
    val discipline = DisciplineDao(database)
    val classDao = ClassDao(database)
    val classGroupDao = ClassGroupDao(database)
    val classLocationDao = ClassLocationDao(database)
    val classItemDao = ClassItemDao(database)
    val classMaterialDao = ClassMaterialDao(database)
    val classAbsenceDao = ClassAbsenceDao(database)
    val gradeDao = GradeDao(database)

    fun <R> transactionWithResult(
        noEnclosing: Boolean = false,
        bodyWithReturn: TransactionWithReturn<R>.() -> R,
    ): R {
        return database.transactionWithResult(noEnclosing, bodyWithReturn)
    }

    fun transaction(
        noEnclosing: Boolean = false,
        body: TransactionWithoutReturn.() -> Unit,
    ) {
        return database.transaction(noEnclosing, body)
    }
}