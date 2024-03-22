package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.Grade
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface GradeRepository : CoroutineCrudRepository<Grade, UUID> {
    @Query("SELECT * FROM grades WHERE student_class_id = :studentClassId AND platform_id = :platformId")
    suspend fun findByStudentClassIdAndPlatformId(studentClassId: UUID, platformId: String): Grade?
}