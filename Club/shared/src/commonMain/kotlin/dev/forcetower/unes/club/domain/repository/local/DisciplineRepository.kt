package dev.forcetower.unes.club.domain.repository.local

import dev.forcetower.unes.club.data.storage.database.ClassItem
import dev.forcetower.unes.club.data.storage.database.ClassMaterial
import dev.forcetower.unes.club.domain.model.disciplines.ClassGroupData
import dev.forcetower.unes.club.domain.model.disciplines.SemesterClassData
import kotlinx.coroutines.flow.Flow

internal interface DisciplineRepository {
    fun getSemesterWithDisciplines(): Flow<List<SemesterClassData>>
    suspend fun calculateScoreSnapshot(): Double?
    suspend fun fetchData(semesterId: Long)
    fun userCalculatedStore(): Flow<Double?>
    suspend fun loadMissingSemesters(fetchState: List<Long>): List<Long>
}