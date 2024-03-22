package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.Grade
import dev.forcetower.unes.reactor.data.model.aggregation.GradeData
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface GradeRepository : CoroutineCrudRepository<Grade, UUID> {
    @Query("SELECT * FROM grades WHERE student_class_id = :studentClassId AND platform_id = :platformId")
    suspend fun findByStudentClassIdAndPlatformId(studentClassId: UUID, platformId: String): Grade?

    @Query(
        """
        SELECT 
            grades.id as id,
            grades.name as name,
            grades.notification_state as notification_state,
            grades.grade as value,
            grades.grade_raw as value_raw,
            disciplines.name as discipline,
            disciplines.code as discipline_code,
            (select semesters.name FROM semesters WHERE id = discipline_offers.semester_id) as semester
        FROM grades
            INNER JOIN student_classes ON grades.student_class_id = student_classes.id
            INNER JOIN classes ON student_classes.class_id = classes.id
            INNER JOIN discipline_offers on classes.offer_id = discipline_offers.id
            INNER JOIN disciplines ON discipline_offers.discipline_id = disciplines.id
            WHERE student_classes.student_id = :studentId AND grades.notification_state <> 0
        """
    )
    suspend fun findGradesPendingNotification(studentId: UUID): List<GradeData>

    @Query("""
        UPDATE grades SET
            notification_state = 0
        WHERE id IN (
            SELECT 
                grades.id as id
            FROM grades
                INNER JOIN student_classes ON grades.student_class_id = student_classes.id
                INNER JOIN classes ON student_classes.class_id = classes.id
                INNER JOIN discipline_offers on classes.offer_id = discipline_offers.id
                INNER JOIN disciplines ON discipline_offers.discipline_id = disciplines.id
                WHERE student_classes.student_id = :studentId
        )
    """)
    suspend fun markGradesNotifiedForStudent(studentId: UUID)
}