package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.Discipline
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime
import java.util.UUID

@Repository
interface DisciplineRepository : CoroutineCrudRepository<Discipline, UUID> {

    @Query("INSERT INTO disciplines(code, name, program, credits, department_id, full_code) VALUES (:code, :name, :program, :credits, :departmentId, :fullCode) ON CONFLICT (department_id,code) DO NOTHING")
    suspend fun insertIgnore(
        code: String,
        name: String,
        program: String?,
        credits: Int,
        departmentId: UUID?,
        fullCode: String?
    )

    @Query("SELECT * FROM disciplines WHERE code = :code AND department_id = :departmentId LIMIT 1")
    suspend fun findByCodeAndDepartmentId(code: String, departmentId: UUID?): Discipline?
}