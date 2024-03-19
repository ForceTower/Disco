package dev.forcetower.unes.club.domain.usecase.auth

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import dev.forcetower.unes.club.domain.repository.local.AccessRepository
import dev.forcetower.unes.club.domain.repository.local.DisciplineRepository
import dev.forcetower.unes.club.domain.repository.local.GradeRepository
import dev.forcetower.unes.club.domain.repository.local.SemesterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull

class ConnectedUserUseCase internal constructor(
    private val repository: AccessRepository,
    private val semesters: SemesterRepository
) {
    @NativeCoroutines
    suspend fun hasAccess() = repository.requireCurrentAccess() != null

    @NativeCoroutines
    fun access() = repository.access()

    @NativeCoroutines
    fun currentProfile() = repository.currentProfile()

    @NativeCoroutines
    suspend fun logout() = repository.logout()

    @NativeCoroutines
    fun semestersCount() = semesters.getCount()
}