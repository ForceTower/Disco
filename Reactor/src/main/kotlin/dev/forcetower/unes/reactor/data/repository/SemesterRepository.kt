package dev.forcetower.unes.reactor.data.repository

import dev.forcetower.unes.reactor.data.entity.Semester
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.UUID

@Repository
interface SemesterRepository : CoroutineCrudRepository<Semester, UUID> {
    @Query("INSERT INTO semesters(name, codename, platform_id, start, finish, start_class, end_class) VALUES (:name, :codename, :platformId, :start, :finish, :startClass, :endClass) on conflict(platform_id) do nothing")
    suspend fun insertIgnore(
        name: String,
        codename: String,
        platformId: Long,
        start: ZonedDateTime?,
        finish: ZonedDateTime?,
        startClass: ZonedDateTime?,
        endClass: ZonedDateTime?
    )

    @Query("SELECT * FROM semesters WHERE platform_id = :id")
    suspend fun findByPlatformId(id: Long): Semester?

    @Query("select semesters.* from semesters" +
            "    inner join discipline_offers offers on semesters.id = offers.semester_id" +
            "    inner join classes c on offers.id = c.offer_id" +
            "    inner join student_classes on c.id = student_classes.class_id" +
            "    inner join student on student_classes.student_id = student.id" +
            "    where student.id = :studentId ORDER BY semesters.start desc LIMIT 1")
    suspend fun findCurrentSemesterForStudent(studentId: UUID): Semester?
}