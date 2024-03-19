package dev.forcetower.unes.club.domain.usecase.disciplines

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import dev.forcetower.unes.club.domain.model.disciplines.SemesterClassData
import dev.forcetower.unes.club.domain.model.disciplines.SemesterFetchState
import dev.forcetower.unes.club.domain.repository.local.DisciplineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetDisciplinesUseCase internal constructor(
    private val repository: DisciplineRepository
) {
    @NativeCoroutines
    fun classData(): Flow<List<SemesterClassData>> {
        return repository.getSemesterWithDisciplines()
    }

    @NativeCoroutines
    fun fetchSemesterData(semester: Long) = flow {
        repository.fetchData(semester)
        emit(SemesterFetchState.Completed)
    }

    @NativeCoroutines
    suspend fun loadMissingSemesters(loaded: List<Long>): List<Long> {
        return repository.loadMissingSemesters(loaded)
    }
}