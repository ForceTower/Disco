package dev.forcetower.unes.club.data.repository.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import dev.forcetower.unes.club.data.processor.LectureProcessor
import dev.forcetower.unes.club.data.processor.MissedLectureProcessor
import dev.forcetower.unes.club.data.storage.database.ClassAbsence
import dev.forcetower.unes.club.data.storage.database.ClassItem
import dev.forcetower.unes.club.data.storage.database.ClassMaterial
import dev.forcetower.unes.club.data.storage.database.GeneralDB
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.domain.model.disciplines.ClassGroupData
import dev.forcetower.unes.club.domain.repository.local.ClassRepository
import dev.forcetower.unes.club.util.primitives.asBoolean
import dev.forcetower.unes.singer.Singer
import dev.forcetower.unes.singer.domain.model.Authorization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class ClassRepositoryImpl(
    private val database: GeneralDatabase,
    private val generalDB: GeneralDB,
    private val singer: Singer
) : ClassRepository {
    override fun getClassData(groupId: Long): Flow<ClassGroupData?> {
        val group = database.classGroupQueries.findByIdWithDependencies(groupId).asFlow()
            .mapToOneOrNull(Dispatchers.IO)

        return group.map {
            it ?: return@map null

            val clazz = database.classQueries.findById(it.classId).executeAsOne()
            val discipline = database.disciplineQueries.findById(clazz.disciplineId).executeAsOne()
            val absences = database.classAbsenceQueries.selectClassCount(clazz.id).executeAsOne()
            ClassGroupData(
                clazz,
                discipline,
                it,
                absences.toInt()
            )
        }
    }

    override fun getMaterials(groupId: Long): Flow<List<ClassMaterial>> {
        return database.classMaterialQueries.getMaterialsFromGroup(groupId).asFlow()
            .mapToList(Dispatchers.IO)
    }

    override fun getItems(groupId: Long): Flow<List<ClassItem>> {
        return database.classItemQueries.getItemsFromGroup(groupId).asFlow()
            .mapToList(Dispatchers.IO)
    }

    override fun getAbsences(groupId: Long): Flow<List<ClassAbsence>> {
        return database.classAbsenceQueries.selectFromGroup(groupId).asFlow()
            .mapToList(Dispatchers.IO)
    }

    override fun fetchData(groupId: Long) = flow {
        val access = database.accessQueries.selectAccess().executeAsOneOrNull()
            ?: throw IllegalStateException("Not connected")

        if (!access.valid.asBoolean()) {
            throw IllegalStateException("Invalid access state")
        }

        val profile = database.profileQueries.selectMe().executeAsOneOrNull()
            ?: throw IllegalStateException("Profile not found")

        val group = database.classGroupQueries.findById(groupId).executeAsOne()
        val platformId = group.platformId
        if (platformId == null) {
            emit(Unit)
            return@flow
        }

        singer.setDefaultAuthorization(Authorization(access.username, access.password))
        val lectures = singer.lectures(platformId)
        LectureProcessor(generalDB, groupId, lectures, false).execute()

        val missed = singer.absences(profile.platformId, platformId)
        MissedLectureProcessor(generalDB, database, profile.id, groupId, missed, false).execute()

        emit(Unit)
    }
}