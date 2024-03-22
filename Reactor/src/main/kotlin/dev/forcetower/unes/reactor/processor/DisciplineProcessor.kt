package dev.forcetower.unes.reactor.processor

import dev.forcetower.breaker.model.DisciplineData
import dev.forcetower.unes.reactor.data.entity.Department
import dev.forcetower.unes.reactor.data.entity.Discipline
import dev.forcetower.unes.reactor.data.entity.DisciplineClass
import dev.forcetower.unes.reactor.data.entity.DisciplineOffer
import dev.forcetower.unes.reactor.data.entity.Semester
import dev.forcetower.unes.reactor.data.entity.Student
import dev.forcetower.unes.reactor.data.entity.StudentClass
import dev.forcetower.unes.reactor.data.repository.DepartmentRepository
import dev.forcetower.unes.reactor.data.repository.DisciplineClassRepository
import dev.forcetower.unes.reactor.data.repository.DisciplineClassTeacherRepository
import dev.forcetower.unes.reactor.data.repository.DisciplineOfferRepository
import dev.forcetower.unes.reactor.data.repository.DisciplineRepository
import dev.forcetower.unes.reactor.data.repository.SemesterRepository
import dev.forcetower.unes.reactor.data.repository.StudentClassRepository
import dev.forcetower.unes.reactor.data.repository.TeacherRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Component
class DisciplineProcessor(
    private val gradeProc: GradeProcessor,
    private val disciplineRepo: DisciplineRepository,
    private val semesterRepo: SemesterRepository,
    private val departmentRepo: DepartmentRepository,
    private val offerRepo: DisciplineOfferRepository,
    private val classRepo: DisciplineClassRepository,
    private val classTeacherRepo: DisciplineClassTeacherRepository,
    private val teacherRepo: TeacherRepository,
    private val studentClassRepo: StudentClassRepository
) {
    private val logger = LoggerFactory.getLogger(DisciplineProcessor::class.java)

    @Transactional
    suspend fun execute(
        student: Student,
        semester: Semester,
        data: List<DisciplineData>,
        notify: Boolean
    ) {
        // this will be null for students that are new.
        val currentSemester = semesterRepo.findCurrentSemesterForStudent(student.id!!)
        logger.debug("Current semester for student {} is {}", student.name, currentSemester?.name)
//        val allocations = mutableListOf<ClassLocation>()

        data.forEach {
            val discipline = createOrFindDiscipline(it)
            val offer = createOrFindOffer(discipline, semester)
            it.classes.forEach { platformClass ->
                val clazz = createOrFindClass(platformClass, offer)
                val studentClass = createOrFindStudentClass(clazz, student, it)
                gradeProc.processGrades(studentClass, student, it.evaluations, notify)
            }
        }
    }

    private suspend fun createOrFindClass(
        platform: dev.forcetower.breaker.model.DisciplineClass,
        offerId: UUID
    ): DisciplineClass {
        val current = classRepo.findByPlatformId(platform.id)
        val classId = current?.id
            ?: classRepo.insertIgnore(
                offerId,
                platform.groupName,
                platform.id,
                platform.hours
            )

        platform.teacher?.let {
            val teacherId = teacherRepo.insertIgnore(
                it.name.trim(),
                it.id
            )
            logger.info("Inserted teacher with id {}", teacherId)
            classTeacherRepo.insertIgnore(classId, teacherId)
        }

        return classRepo.findById(classId)!!
    }

    private suspend fun createOrFindStudentClass(
        clazz: DisciplineClass,
        student: Student,
        data: DisciplineData
    ): UUID {
        val studentClassId = studentClassRepo.upsert(
            student.id!!,
            clazz.id,
            data.result?.mean?.let { BigDecimal.valueOf(it) },
            data.result?.mean?.toString(),
        )

        return studentClassId
    }

    private suspend fun createOrFindOffer(discipline: Discipline, semester: Semester): UUID {
        val disciplineId = discipline.id!!
        val semesterId = semester.id!!
        val current = offerRepo.findByDisciplineIdAndSemesterId(disciplineId, semesterId)
        if (current != null) return current.id

        return offerRepo.insertIgnore(disciplineId, semesterId)
    }

    private suspend fun createOrFindDepartment(discipline: DisciplineData): Department? {
        val department = discipline.department?.trim() ?: return null
        val code = discipline.code.replace(Regex("[^A-Za-z ]"), "")

        val current = departmentRepo.findByCode(code)
        if (current != null) return current

        runCatching {
            departmentRepo.save(
                Department(
                    name = department,
                    code = code,
                    null,
                    null,
                    null
                ).also { it.setNew() }
            )
        }

        return departmentRepo.findByCode(code)
    }

    private suspend fun createOrFindDiscipline(discipline: DisciplineData): Discipline {
        val department = createOrFindDepartment(discipline)
        val current = disciplineRepo.findByCodeAndDepartmentId(discipline.code, department?.id)
        if (current != null) return current

        logger.warn("Department: $department")
        val resume = if (discipline.program.isNullOrBlank()) null else discipline.program
        disciplineRepo.insertIgnore(
            code = discipline.code.trim(),
            name = discipline.name.trim(),
            program = resume?.trim(),
            credits = discipline.hours,
            departmentId = department?.id,
            fullCode = discipline.code.trim()
        )
        return disciplineRepo.findByCodeAndDepartmentId(discipline.code, department?.id)!!.also {
            logger.info("Returning discipline $it")
        }
    }
}