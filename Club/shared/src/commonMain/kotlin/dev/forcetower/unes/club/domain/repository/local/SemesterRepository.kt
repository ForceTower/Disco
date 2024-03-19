package dev.forcetower.unes.club.domain.repository.local

import kotlinx.coroutines.flow.Flow

interface SemesterRepository {
    fun getCount(): Flow<Int>
}