package dev.forcetower.unes.reactor.controller.student

import dev.forcetower.unes.reactor.data.repository.CourseRepository
import dev.forcetower.unes.reactor.data.repository.StudentRepository
import dev.forcetower.unes.reactor.domain.dto.BaseResponse
import dev.forcetower.unes.reactor.domain.dto.student.PublicStudent
import dev.forcetower.unes.reactor.service.snowpiercer.SnowpiercerUpdateService
import dev.forcetower.unes.reactor.utils.spring.requireUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api/student")
class StudentController(
    private val students: StudentRepository,
    private val courses: CourseRepository,
    private val update: SnowpiercerUpdateService
) {
    @GetMapping("/me")
    suspend fun me(): ResponseEntity<BaseResponse> {
        val user = requireUser()
        val student = students.findByUserId(user.id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found")

        val id = student.id ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found")
        val course = courses.findCourseByStudentId(id)

        return ResponseEntity.ok(
            BaseResponse.ok(
                PublicStudent(
                    id,
                    student.name,
                    course?.id,
                    course?.name
                )
            )
        )
    }

    @GetMapping("/sync")
    suspend fun update(): ResponseEntity<*> {
        val user = requireUser()
        val student = students.findByUserId(user.id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found")

        update.update(student, true)
        return ResponseEntity.ok(BaseResponse.ok("Doing the thing!"))
    }
}