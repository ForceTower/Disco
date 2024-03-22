package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.DisciplineClassTeacher
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DisciplineClassTeacherRepository : CoroutineCrudRepository<DisciplineClassTeacher, UUID> {
    @Query("INSERT INTO class_teacher(class_id, teacher_id) VALUES (:classId, :teacherId) ON CONFLICT (class_id,teacher_id) DO NOTHING RETURNING id")
    suspend fun insertIgnore(classId: UUID, teacherId: UUID): UUID
}