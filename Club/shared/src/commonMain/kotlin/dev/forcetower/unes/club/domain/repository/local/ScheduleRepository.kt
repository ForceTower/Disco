package dev.forcetower.unes.club.domain.repository.local

import dev.forcetower.unes.club.domain.model.schedule.ClassLocationData
import dev.forcetower.unes.club.domain.model.schedule.ExtendedClassLocationData
import dev.forcetower.unes.club.domain.model.schedule.ProcessedClassLocation
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getCurrentVisibleSchedule(): Flow<Map<Int, List<ProcessedClassLocation>>>
    suspend fun currentClass(): ExtendedClassLocationData?
}