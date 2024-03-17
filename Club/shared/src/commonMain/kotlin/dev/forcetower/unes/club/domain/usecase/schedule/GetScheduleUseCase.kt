package dev.forcetower.unes.club.domain.usecase.schedule

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import dev.forcetower.unes.club.domain.model.schedule.BlockLine
import dev.forcetower.unes.club.domain.model.schedule.BlockSchedule
import dev.forcetower.unes.club.domain.model.schedule.LineSchedule
import dev.forcetower.unes.club.domain.model.schedule.LinedClassLocation
import dev.forcetower.unes.club.domain.model.schedule.ProcessedClassLocation
import dev.forcetower.unes.club.domain.model.schedule.ScheduleData
import dev.forcetower.unes.club.domain.repository.local.ScheduleRepository
import dev.forcetower.unes.club.extensions.toLongWeekDay
import dev.forcetower.unes.club.extensions.toWeekDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetScheduleUseCase(
    private val repository: ScheduleRepository
) {
    @NativeCoroutines
    fun currentSchedule(showEmptyDays: Boolean): Flow<ScheduleData> {
        return repository.getCurrentVisibleSchedule()
            .map {
                ScheduleData(
                    original = it,
                    block = mapScheduleToBlocks(it, showEmptyDays),
                    line = buildScheduleLine(it)
                )
            }
    }

    private fun mapScheduleToBlocks(
        values: Map<Int, List<ProcessedClassLocation>>,
        showEmptyDays: Boolean
    ): BlockSchedule {
        var colorIndex = 0
        val disciplineColors = mutableMapOf<String, Int>()
        val result = mutableListOf<ProcessedClassLocation>()
        val result2 = mutableListOf<BlockLine>()
        val referenceList = values[-1].orEmpty()

        val mutatedMap = values.toMutableMap().apply {
            if (showEmptyDays) {
                // Never on sunday or saturday, unless if already added
                (2..6).forEach {
                    if (!values.containsKey(it)) {
                        put(it, referenceList.map { ProcessedClassLocation.EmptySpace() })
                    }
                }
            }
        }.toMap()

        // The first row contains days
        result.add(ProcessedClassLocation.EmptySpace(true))
        result += mutatedMap.keys.sortedBy { it }.filter { it != -1 }.map {
            ProcessedClassLocation.DaySpace(it.toWeekDay(), it)
        }

        val items = buildList {
            add(ProcessedClassLocation.EmptySpace(true))
            addAll(mutatedMap.keys.sortedBy { it }.filter { it != -1 }.map {
                ProcessedClassLocation.DaySpace(it.toWeekDay(), it)
            })
        }
        result2.add(BlockLine(-1, items))

        // the following rows contains time followed by locations
        val referenceMap = mutatedMap.entries.filter { it.key != -1 }.sortedBy { it.key }
        referenceList.forEachIndexed { index, element ->
            val l2 = mutableListOf<ProcessedClassLocation>()
            result.add(element)
            l2.add(element)
            referenceMap.forEach {
                val location = it.value[index]
                result.add(location)
                if (location is ProcessedClassLocation.ElementSpace) {
                    val code = location.reference.discipline.code
                    if (!disciplineColors.containsKey(code)) {
                        disciplineColors[code] = colorIndex++
                    }
                }
                l2.add(location)
            }
            result2.add(BlockLine(index, l2))
        }
        return BlockSchedule(result, result2, disciplineColors)
    }

    private fun buildScheduleLine(value: Map<Int, List<ProcessedClassLocation>>): LineSchedule {
        val list = value.filter { it.key != -1 }
            .mapValues { entry ->
                val mapped = entry.value
                    .filterIsInstance<ProcessedClassLocation.ElementSpace>()
                    .map { LinedClassLocation.ElementSpace(it.reference) }

                buildList {
                    add(LinedClassLocation.DaySpace(entry.key.toLongWeekDay(), entry.key))
                    addAll(mapped)
                }
            }.entries.sortedBy { it.key }.flatMap { it.value }

        return LineSchedule(list)
    }
}