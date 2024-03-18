package dev.forcetower.unes.club.data.processor

import com.benasher44.uuid.uuid4
import dev.forcetower.unes.club.data.storage.database.Class
import dev.forcetower.unes.club.data.storage.database.ClassGroup
import dev.forcetower.unes.club.data.storage.database.ClassLocation
import dev.forcetower.unes.club.data.storage.database.Discipline
import dev.forcetower.unes.club.data.storage.database.GeneralDB
import dev.forcetower.unes.club.data.storage.database.Semester
import dev.forcetower.unes.club.data.storage.database.Teacher
import dev.forcetower.unes.club.extensions.createTimeInt
import dev.forcetower.unes.club.extensions.removeSeconds
import dev.forcetower.unes.club.extensions.toTitleCase
import dev.forcetower.unes.club.extensions.toWeekDay
import dev.forcetower.unes.singer.data.model.dto.DisciplineData
import dev.forcetower.unes.singer.data.model.dto.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class DisciplinesProcessor(
    private val database: GeneralDB,
    private val disciplines: List<DisciplineData>,
    private val semesterId: Long,
    private val localProfileId: Long,
    private val notify: Boolean
) {
    suspend fun execute() = withContext(Dispatchers.IO) {
        database.transactionWithResult {
            executeInTransaction()
        }
    }

    private fun executeInTransaction() {
        val currentSemester = findCurrentSemester()
        val allocations = mutableListOf<ClassLocation>()

        disciplines.forEach {
            val resume = if (it.program.isNullOrBlank()) null else it.program
            val discipline = Discipline(
                id = 0L,
                name = it.name.trim(),
                code = it.code.trim(),
                credits = it.hours.toLong(),
                resume = resume?.trim(),
                department = it.department?.trim()?.toTitleCase(),
                shortText = null
            )
            val disciplineId = database.discipline.insertOrUpdate(discipline)
            println("Discipline id inserted: $disciplineId at $semesterId")
            val bound = Class(
                id = 0L,
                disciplineId = disciplineId,
                semesterId = semesterId,
                scheduleOnly = 0,
                missedClasses = (it.result?.missedClasses ?: 0).toLong(),
                finalScore = it.result?.mean,
                lastClass = "",
                nextClass = "",
                partialScore = null,
                status = null
            )

            val classId = database.classDao.insertNewWays(bound)
            it.classes.forEach { clazz ->
                val teacherId = insertTeacher(clazz.teacher, it.department)
                val group = ClassGroup(
                    id = 0L,
                    classId = classId,
                    credits = clazz.hours.toLong(),
                    draft = 0,
                    group = clazz.groupName.trim(),
                    teacher = clazz.teacher?.name?.toTitleCase(),
                    platformId = clazz.id,
                    teacherId = teacherId,
                    teacherEmail = clazz.teacher?.email,
                    ignored = 0
                )
                val groupId = database.classGroupDao.insertNewWay(group)
                println("Group id: $groupId")
                if (currentSemester?.id == semesterId) {
                    clazz.allocations.forEach { allocation ->
                        val time = allocation.time
                        if (time != null) {
                            allocations.add(
                                ClassLocation(
                                    id = 0L,
                                    groupId = groupId,
                                    campus = allocation.space?.campus?.trim(),
                                    modulo = allocation.space?.modulo?.trim(),
                                    room = allocation.space?.location?.trim(),
                                    day = (time.day + 1).toWeekDay(),
                                    dayInt = (time.day + 1).toLong(),
                                    startsAt = time.start.removeSeconds(),
                                    endsAt = time.end.removeSeconds(),
                                    startsAtInt = time.start.createTimeInt().toLong(),
                                    endsAtInt = time.end.createTimeInt().toLong(),
                                    profileId = localProfileId,
                                    hiddenOnSchedule = 0L,
                                    uuid = uuid4().toString()
                                )
                            )
                        }
                    }
                    LectureProcessor(database, groupId, clazz.lectures, notify).executeInTransaction()
                }
            }
            database.gradeDao.putGradesNewWay(classId, it.evaluations, notify)
        }

        database.classLocationDao.putNewSchedule(expandLocations(allocations))
    }

    private fun insertTeacher(teacher: Person?, department: String?): Long? {
        teacher ?: return null
        val value = Teacher(
            0,
            teacher.name,
            teacher.email,
            teacher.id,
            department
        )

        return database.teacherDao.insertOrUpdate(value)
    }

    private fun findCurrentSemester(): Semester? {
        val allSemesters = database.semesterDao.selectAll()
        return if (allSemesters.all { it.start != null }) {
            allSemesters.maxByOrNull { it.start!! }
        } else {
            allSemesters.maxByOrNull { it.platformId }
        }
    }

    companion object {
        fun expandLocations(locations: List<ClassLocation>): List<ClassLocation> {
            val starts = locations.groupBy { it.startsAtInt }.mapValues { it.value.first().startsAt }
            val ends = locations.groupBy { it.endsAtInt }.mapValues { it.value.first().endsAt }
            val allMapped = starts + ends
            val allTimes = allMapped.keys.toList().sorted()

            println("All Mapped: $allMapped")
            println("All times $allTimes")

            return locations.flatMap { location ->
                val result = mutableListOf<ClassLocation>()
                var start = location.startsAtInt
                var index = allTimes.indexOf(start) + 1
                var end = allTimes[index]

                println("About to expand $location")

                while (location.endsAtInt != end) {
                    result += location.copy(
                        startsAt = allMapped.getValue(start),
                        startsAtInt = start,
                        endsAt = allMapped.getValue(end),
                        endsAtInt = end
                    )
                    index++
                    start = end
                    // this wont index out of bounds since the "location.endsAtInt != end"
                    // will be fulfilled once we reach the end of the array
                    end = allTimes[index]
                }

                result += location.copy(
                    startsAt = allMapped.getValue(start),
                    startsAtInt = start,
                    endsAt = allMapped.getValue(end),
                    endsAtInt = end
                )

                println("Expanded into: $result")
                result
            }
        }
    }
}