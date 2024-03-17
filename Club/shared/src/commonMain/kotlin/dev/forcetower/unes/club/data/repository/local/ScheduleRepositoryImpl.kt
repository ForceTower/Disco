package dev.forcetower.unes.club.data.repository.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.domain.model.schedule.ClassLocationData
import dev.forcetower.unes.club.domain.model.schedule.ProcessedClassLocation
import dev.forcetower.unes.club.domain.repository.local.ScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal class ScheduleRepositoryImpl(
    private val database: GeneralDatabase
) : ScheduleRepository {
    override fun getCurrentVisibleSchedule(): Flow<Map<Int, List<ProcessedClassLocation>>> {
//        val semesters = database.semesterQueries.selectParticipating().asFlow().mapToList(Dispatchers.IO)
        val disciplines = database.disciplineQueries.selectAll().asFlow().mapToList(Dispatchers.IO)
        val classes = database.classQueries.selectParticipatingClasses().asFlow().mapToList(Dispatchers.IO)
        val groups = database.classGroupQueries.selectAll().asFlow().mapToList(Dispatchers.IO)
        val locations = database.classLocationQueries.selectVisibleSchedule().asFlow().mapToList(Dispatchers.IO)

        return combine(disciplines, classes, groups, locations) { dcp, cls, grp, data ->
            val timers = data.map { Timed(it.startsAtInt.toInt(), it.endsAtInt.toInt(), it.startsAt, it.endsAt) }.distinctBy { it.start }.sortedBy { it.start }
            data.groupBy { it.dayInt.toInt() }.mapValues { entry ->
                val dayList = timers.map { timed ->
                    val location = entry.value.find { it.startsAtInt.toInt() == timed.start && it.endsAtInt.toInt() == timed.end }
                    val element = if (location == null)
                        ProcessedClassLocation.EmptySpace()
                    else {
                        val group = grp.first { it.id == location.groupId }
                        val clazz = cls.first { it.id == group.classId }
                        ProcessedClassLocation.ElementSpace(
                            ClassLocationData(
                                location,
                                group,
                                clazz,
                                dcp.first { it.id == clazz.disciplineId }
                            )
                        )
                    }
                    element
                }
                dayList
            }.toMutableMap().apply {
                put(-1, timers.map { ProcessedClassLocation.TimeSpace(it.startString, it.endString, it.start, it.end) })
            }.toMap()
        }
    }

    private data class Timed(
        val start: Int,
        val end: Int,
        val startString: String,
        val endString: String
    )
}