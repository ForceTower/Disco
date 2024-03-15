package dev.forcetower.unes.club.domain.usecase

import dev.forcetower.unes.club.data.storage.database.Access
import dev.forcetower.unes.club.domain.repository.local.AccessRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class InsertAccessUseCase(
    private val repository: AccessRepository
) {
    suspend operator fun invoke(username: String, password: String) {
        repository.insert(username, password)
    }

    fun flowing() = flow {
        emit(1)
        delay(1000L)
        emit(2)
    }

    suspend fun require(): Access? {
        return repository.requireCurrentAccess()
    }
}