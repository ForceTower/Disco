package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.Student
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface StudentRepository : CoroutineCrudRepository<Student, String> {
    suspend fun findStudentByPlatformId(platformId: Long): Student?
    suspend fun findByUserId(id: UUID): Student?
}