package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.StudentClass
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.UUID

@Repository
interface StudentClassRepository : CoroutineCrudRepository<StudentClass, UUID> {
    @Query("SELECT * FROM student_classes WHERE student_id = :studentId AND class_id = :classId")
    suspend fun findByStudentIdAndClassId(studentId: UUID, classId: UUID): StudentClass?

    @Query("INSERT INTO student_classes(student_id, class_id, final_grade, final_grade_raw, partial_grade, partial_grade_raw) VALUES (:studentId, :classId, :finalGrade, :finalGradeRaw, null, null) ON CONFLICT (student_id, class_id) DO UPDATE SET final_grade = excluded.final_grade, final_grade_raw = excluded.final_grade_raw RETURNING id")
    suspend fun upsert(
        studentId: UUID,
        classId: UUID,
        finalGrade: BigDecimal?,
        finalGradeRaw: String?
    ): UUID
}