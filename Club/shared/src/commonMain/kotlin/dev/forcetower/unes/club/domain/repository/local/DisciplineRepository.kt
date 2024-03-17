package dev.forcetower.unes.club.domain.repository.local

import dev.forcetower.unes.club.domain.model.disciplines.SemesterClassData
import kotlinx.coroutines.flow.Flow

internal interface DisciplineRepository {
    fun getSemesterWithDisciplines(): Flow<List<SemesterClassData>>
    suspend fun fetchData(semesterId: Long)
}