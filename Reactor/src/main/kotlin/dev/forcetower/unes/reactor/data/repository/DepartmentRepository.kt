package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.Department
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DepartmentRepository : CoroutineCrudRepository<Department, UUID> {
    @Query("SELECT * FROM departments WHERE code = :code LIMIT 1")
    suspend fun findByCode(code: String): Department?
}