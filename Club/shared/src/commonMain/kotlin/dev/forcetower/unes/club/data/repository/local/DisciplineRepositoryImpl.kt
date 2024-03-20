package dev.forcetower.unes.club.data.repository.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.forcetower.unes.club.data.processor.DisciplinesProcessor
import dev.forcetower.unes.club.data.processor.SemestersProcessor
import dev.forcetower.unes.club.data.storage.database.GeneralDB
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.domain.model.disciplines.ClassData
import dev.forcetower.unes.club.domain.model.disciplines.ProcessedGrade
import dev.forcetower.unes.club.domain.model.disciplines.SemesterClassData
import dev.forcetower.unes.club.domain.repository.local.DisciplineRepository
import dev.forcetower.unes.club.extensions.round
import dev.forcetower.unes.club.util.flow.combine
import dev.forcetower.unes.club.util.primitives.asBoolean
import dev.forcetower.unes.singer.Singer
import dev.forcetower.unes.singer.data.model.dto.Semester
import dev.forcetower.unes.singer.domain.model.Authorization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

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

    override suspend fun calculateScoreSnapshot() = withContext(Dispatchers.IO) {
        val snapshot = getSemesterWithDisciplinesSnapshot()
        calculateScore(snapshot)
    }

    private suspend fun getSemesterWithDisciplinesSnapshot(): List<SemesterClassData> = withContext(Dispatchers.IO) {
        val semesters = database.semesterQueries.selectParticipating().executeAsList()
        val disciplines = database.disciplineQueries.selectAll().executeAsList()
        val classes = database.classQueries.selectParticipatingClasses().executeAsList()
        val groups = database.classGroupQueries.selectAll().executeAsList()
        val grades = database.gradeQueries.selectAll().executeAsList()
        val absences = database.classAbsenceQueries.selectAll().executeAsList()

        semesters.map { semester ->
            SemesterClassData(
                semester,
                classes.filter { it.semesterId == semester.id }.mapNotNull { clazz ->
                    // early return null due to concurrent modifications during transactions :^)
                    val discipline = disciplines.firstOrNull { it.id == clazz.disciplineId } ?: return@mapNotNull null
                    ClassData(
                        clazz,
                        discipline,
                        groups.filter { it.classId == clazz.id },
                        absences.filter { it.classId == clazz.id }.size,
                        grades.filter { it.classId == clazz.id }.map { grade ->
                            ProcessedGrade(grade)
                        }
                    )
                }
            )
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
        calculateScoreSnapshot()
    }

    override suspend fun loadMissingSemesters(fetchState: List<Long>): List<Long> {
        val access = database.accessQueries.selectAccess().executeAsOneOrNull()
            ?: throw IllegalStateException("No access...")
        if (!access.valid.asBoolean()) throw IllegalStateException("Access is not valid")

        val profile = database.profileQueries.selectMe().executeAsOneOrNull()
            ?: throw IllegalStateException("No profile set")

        singer.setDefaultAuthorization(Authorization(access.username, access.password))

        val semesters = database.semesterQueries.selectAll().executeAsList()
        val missing = basicSemesters.filter { semesters.none { s -> s.platformId == it.id } }
        val processed = mutableListOf<Long>()
        missing.forEach { semester ->
            if (fetchState.contains(semester.id)) {
                return@forEach
            }
            val result = runCatching {
                singer.grades(profile.platformId, semester.id)
            }.getOrNull()

            result?.let {
                if (it.isNotEmpty()) {
                    SemestersProcessor(listOf(semester), database).execute()
                    val currentSemesterIns =
                        database.semesterQueries.selectSemester(semester.id).executeAsOne()
                    DisciplinesProcessor(
                        generalDB,
                        result,
                        currentSemesterIns.id,
                        profile.id,
                        false
                    ).execute()
                }
                processed.add(semester.id)
            }
        }

        if (missing.isNotEmpty()) {
            calculateScoreSnapshot()
        }
        return (fetchState + processed).distinct()
    }

    override fun userCalculatedStore(): Flow<Double?> {
        return getSemesterWithDisciplines().map {
            calculateScore(it)
        }
    }

    private fun calculateScore(data: List<SemesterClassData>): Double? {
        val classes = data.flatMap { it.classes }.filter { it.clazz.finalScore != null }
        val hours = classes.sumOf { it.discipline.credits }
        val mean = classes.sumOf {
            val zeroValue = it.clazz.missedClasses > (it.discipline.credits / 4)
            val finalScore = if (zeroValue) 0.0 else it.clazz.finalScore!!
            it.discipline.credits * finalScore
        }
        if (hours > 0) {
            val score = (mean / hours).round(1)
            generalDB.profileDao.updateCalculatedScore(score)
            return score
        }
        return null
    }

    companion object {
        private val basicSemesters = listOf(
            Semester(1000000792, "20181", "20181", "2018-03-19T00:00:00-03:00", "2018-08-08T00:00:00-03:00"),
            Semester(1000000754, "20172", "20172", "2017-09-11T00:00:00-03:00", "2018-02-21T00:00:00-03:00"),
            Semester(1000000713, "20171", "20171", "2017-03-13T00:00:00-03:00", "2017-08-19T00:00:00-03:00"),
            Semester(1000000679, "20152 F", "20152 - Extra", "2016-05-30T00:00:00-03:00", "2016-07-01T00:00:00-03:00"),
            Semester(1000000623, "20152", "20152", "2015-11-20T00:00:00-02:00", "2016-05-16T00:00:00-03:00"),
            Semester(1000000594, "20151", "20151", "2015-03-03T00:00:00-03:00", "2015-10-29T00:00:00-02:00"),
            Semester(1000000553, "20142", "2014.2", "2014-08-25T00:00:00-03:00", "2014-12-23T00:00:00-02:00"),
            Semester(1000000486, "20141", "2014.1", "2014-03-10T00:00:00-03:00", "2014-08-02T00:00:00-03:00"),
            Semester(1000000532, "20132 F", "20132-Férias", "2014-01-14T00:00:00-02:00", "2014-02-13T00:00:00-02:00"),
            Semester(1000000483, "20132", "2013.2", "2013-08-26T00:00:00-03:00", "2014-01-06T00:00:00-02:00"),
            Semester(1000000439, "20131", "2013.1", "2013-03-11T00:00:00-03:00", "2013-08-15T00:00:00-03:00"),
            Semester(1000000403, "20122", "2012.2", "2012-09-05T00:00:00-03:00", "2013-01-23T00:00:00-02:00"),
            Semester(1000000372, "20121", "2012.1", "2012-04-09T00:00:00-03:00", "2012-08-27T00:00:00-03:00"),
            Semester(1000000340, "20112", "2011.2", "2011-09-29T00:00:00-03:00", "2012-03-19T00:00:00-03:00"),
        )
    }
}