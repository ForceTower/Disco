package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.Course
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CourseRepository : CoroutineCrudRepository<Course, UUID> {
    @Query("SELECT C.* from course C INNER JOIN student_course SC ON SC.course_id = C.id WHERE SC.student_id = :studentId LIMIT 1")
    suspend fun findCourseByStudentId(studentId: UUID): Course?
}