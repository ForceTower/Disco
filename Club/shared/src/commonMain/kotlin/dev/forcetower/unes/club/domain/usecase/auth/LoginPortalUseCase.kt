package dev.forcetower.unes.club.domain.usecase.auth

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import dev.forcetower.unes.club.data.processor.DisciplinesProcessor
import dev.forcetower.unes.club.data.processor.MessagesProcessor
import dev.forcetower.unes.club.data.processor.SemestersProcessor
import dev.forcetower.unes.club.data.storage.database.GeneralDB
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.data.storage.database.PlatformCourse
import dev.forcetower.unes.club.domain.model.auth.LoginFailReason
import dev.forcetower.unes.club.domain.model.auth.LoginState
import dev.forcetower.unes.club.domain.repository.local.AccessRepository
import dev.forcetower.unes.club.extensions.toTitleCase
import dev.forcetower.unes.singer.Singer
import dev.forcetower.unes.singer.domain.exception.InvalidLoginCredentialException
import dev.forcetower.unes.singer.domain.exception.LoginDevException
import dev.forcetower.unes.singer.domain.model.Authorization
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginPortalUseCase internal constructor(
    private val repository: AccessRepository,
    private val general: GeneralDB,
    private val database: GeneralDatabase,
    private val singer: Singer
) {
    @NativeCoroutines
    @Throws(InvalidLoginCredentialException::class, IllegalStateException::class)
    fun doLogin(username: String, password: String): Flow<LoginState> {
        return flow {
            emit(LoginState.Handshake)

            val auth = Authorization(username, password)
            val login = runCatching { singer.me(auth) }
                .onFailure {
                    if (it is InvalidLoginCredentialException) {
                        emit(LoginState.LoginFailed(LoginFailReason.InvalidCredentials, it.message))
                        return@flow
                    }

                    if (it is LoginDevException) {
                        emit(LoginState.LoginFailed(LoginFailReason.DeveloperError, it.message))
                        return@flow
                    }
                }

            val person = login.getOrThrow()

            emit(LoginState.Connected(person))

            repository.insert(username, password)

            singer.setDefaultAuthorization(auth)

            val course = runCatching {
                singer.course(person.id)
            }.getOrNull()

            val profileId = general.profileDao.insert(person, course?.name)

            if (course != null) {
                database.platformCourseQueries.insertItem(
                    PlatformCourse(0, course.id, course.name.toTitleCase(), course.resumedName)
                )
            }

            val messages = singer.messages(person.id)
            MessagesProcessor(messages, database, true).execute()
            emit(LoginState.Messages)

            val semesters = singer.semesters(person.id)
            SemestersProcessor(semesters, database).execute()
            emit(LoginState.Semesters)

            val current = semesters.maxByOrNull { it.start } ?: return@flow
            emit(LoginState.Grades)

            val disciplines = singer.grades(person.id, current.id)
            val semester = database.semesterQueries.selectSemester(current.id).executeAsOne()
            DisciplinesProcessor(general, disciplines, semester.id, profileId, false).execute()

            emit(LoginState.Completed)
        }
    }
}