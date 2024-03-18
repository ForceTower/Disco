package dev.forcetower.unes.club.data.repository.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import dev.forcetower.unes.club.data.processor.DisciplinesProcessor
import dev.forcetower.unes.club.data.storage.database.GeneralDB
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.domain.model.disciplines.ClassData
import dev.forcetower.unes.club.domain.model.disciplines.ClassGroupData
import dev.forcetower.unes.club.domain.model.disciplines.ProcessedGrade
import dev.forcetower.unes.club.domain.model.disciplines.SemesterClassData
import dev.forcetower.unes.club.domain.repository.local.DisciplineRepository
import dev.forcetower.unes.club.util.flow.combine
import dev.forcetower.unes.club.util.primitives.asBoolean
import dev.forcetower.unes.singer.Singer
import dev.forcetower.unes.singer.domain.model.Authorization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DisciplineRepositoryImpl(
    private val database: GeneralDatabase,
    private val generalDB: GeneralDB,
    private val singer: Singer
) : DisciplineRepository {
    override fun getSemesterWithDisciplines(): Flow<List<SemesterClassData>> {
        val semesters = database.semesterQueries.selectParticipating().asFlow().mapToList(Dispatchers.IO)
        val disciplines = database.disciplineQueries.selectAll().asFlow().mapToList(Dispatchers.IO)
        val classes = database.classQueries.selectParticipatingClasses().asFlow().mapToList(Dispatchers.IO)
        val groups = database.classGroupQueries.selectAll().asFlow().mapToList(Dispatchers.IO)
        val grades = database.gradeQueries.selectAll().asFlow().mapToList(Dispatchers.IO)
        val absences = database.classAbsenceQueries.selectAll().asFlow().mapToList(Dispatchers.IO)

        return combine(semesters, disciplines, classes, groups, grades, absences) { sem, dis, cls, grp, grd, abs ->
            sem.map { semester ->
                SemesterClassData(
                    semester,
                    cls.filter { it.semesterId == semester.id }.mapNotNull { clazz ->
                        // early return null due to concurrent modifications during transactions :^)
                        val discipline = dis.firstOrNull { it.id == clazz.disciplineId } ?: return@mapNotNull null
                        ClassData(
                            clazz,
                            discipline,
                            grp.filter { it.classId == clazz.id },
                            abs.filter { it.classId == clazz.id }.size,
                            grd.filter { it.classId == clazz.id }.map { grade ->
                                ProcessedGrade(grade)
                            }
                        )
                    }
                )
            }
        }
    }

    override suspend fun fetchData(semesterId: Long) {
        val access = database.accessQueries.selectAccess().executeAsOneOrNull()
            ?: throw IllegalStateException("No access...")
        if (!access.valid.asBoolean()) throw IllegalStateException("Access is not valid")

        val profile = database.profileQueries.selectMe().executeAsOneOrNull()
            ?: throw IllegalStateException("No profile set")

        val semester = database.semesterQueries.findById(semesterId).executeAsOneOrNull()
            ?: throw IllegalStateException("No semester.")

        singer.setDefaultAuthorization(Authorization(access.username, access.password))
        val result = singer.grades(profile.platformId, semester.platformId)
        DisciplinesProcessor(generalDB, result, semester.id, profile.id, false).execute()
    }
}