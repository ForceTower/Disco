package dev.forcetower.unes.club.domain.usecase

import dev.forcetower.unes.club.data.storage.database.Access
import dev.forcetower.unes.club.domain.repository.local.AccessRepository
import dev.forcetower.unes.singer.Singer
import dev.forcetower.unes.singer.data.model.dto.Semester
import dev.forcetower.unes.singer.data.model.dto.Person
import dev.forcetower.unes.singer.domain.model.SingerAuthorization

class InsertAccessUseCase(
    private val repository: AccessRepository,
    private val singer: Singer
) {
    suspend operator fun invoke(username: String, password: String) {
        repository.insert(username, password)
    }

    suspend fun me(username: String, password: String): Person {
        return singer.me(SingerAuthorization(username, password))
    }

    fun setSingerAuth(auth: SingerAuthorization) {
        singer.setDefaultAuthorization(auth)
    }

    suspend fun semesters(id: Long): List<Semester> {
        return singer.semesters(id)
    }

    suspend fun require(): Access? {
        return repository.requireCurrentAccess()
    }
}