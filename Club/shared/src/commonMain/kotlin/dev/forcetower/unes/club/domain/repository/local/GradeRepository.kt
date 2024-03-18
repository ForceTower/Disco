package dev.forcetower.unes.club.domain.repository.local

import dev.forcetower.unes.club.domain.model.grade.GradeData

interface GradeRepository {
    suspend fun getPendingNotifications(markNotified: Boolean): List<GradeData>
    suspend fun markAllNotified()
}