package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.Teacher
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TeacherRepository : CoroutineCrudRepository<Teacher, UUID> {
    @Query("INSERT INTO teachers (name, platform_id) VALUES (:name, :platformId) ON CONFLICT (platform_id) DO UPDATE SET name = excluded.name RETURNING id")
    suspend fun insertIgnore(name: String, platformId: Long): UUID
}