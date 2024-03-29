package dev.forcetower.unes.reactor.service.snowpiercer

import dev.forcetower.unes.reactor.data.entity.Student
import dev.forcetower.unes.reactor.data.entity.User
import dev.forcetower.unes.reactor.data.entity.UserSettings
import dev.forcetower.unes.reactor.data.repository.StudentRepository
import dev.forcetower.unes.reactor.data.repository.UserRepository
import dev.forcetower.unes.reactor.data.repository.UserSettingsRepository
import dev.forcetower.unes.reactor.utils.extension.toTitleCase
import io.github.scru128.Scru128
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SnowpiercerAuthService(
    private val users: UserRepository,
    private val students: StudentRepository,
    private val settings: UserSettingsRepository,
    private val login: SnowpiercerLoginService
) {
    suspend fun login(username: String, password: String): User? {
        val person = login.login(username, password) ?: return null
        val student = withContext(Dispatchers.IO) {
            students.findStudentByPlatformId(person.id)
        }

        if (student == null) {
            val user = withContext(Dispatchers.IO) {
                val generated = "user_${Scru128.generate()}"
                users.insert(generated, person.name.toTitleCase(), null)
                val user = users.findUserByUsername(generated)!!
                settings.createForUser(user.id)
                user
            }

            val newStudent = Student(UUID.randomUUID(), person.name.toTitleCase(), person.id, user.id).apply { setNew() }
            withContext(Dispatchers.IO) {
                students.save(newStudent)
            }
            return user
        } else {
            return users.findById(student.userId)
        }

    }
}