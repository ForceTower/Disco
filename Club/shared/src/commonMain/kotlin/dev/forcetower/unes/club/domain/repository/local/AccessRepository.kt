package dev.forcetower.unes.club.domain.repository.local

interface AccessRepository {
    suspend fun insert(username: String, password: String)
}