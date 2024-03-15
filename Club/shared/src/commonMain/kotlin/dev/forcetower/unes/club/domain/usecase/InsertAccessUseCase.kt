package dev.forcetower.unes.club.domain.usecase

import dev.forcetower.unes.club.data.storage.database.Access
import dev.forcetower.unes.club.domain.repository.local.AccessRepository

class InsertAccessUseCase(
    private val repository: AccessRepository
) {
    suspend operator fun invoke(username: String, password: String) {
        repository.insert(username, password)
    }

    suspend fun require(): Access? {
        return repository.requireCurrentAccess()
    }
}