package dev.forcetower.unes.club.domain.usecase.auth

import dev.forcetower.unes.club.data.processor.DisciplinesProcessor
import dev.forcetower.unes.club.data.processor.MessagesProcessor
import dev.forcetower.unes.club.data.processor.SemestersProcessor
import dev.forcetower.unes.club.data.storage.database.GeneralDB
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.domain.model.LoginState
import dev.forcetower.unes.club.domain.repository.local.AccessRepository
import dev.forcetower.unes.club.util.flow.CommonFlow
import dev.forcetower.unes.singer.Singer
import dev.forcetower.unes.singer.domain.model.Authorization
import kotlinx.coroutines.flow.flow

class LoginPortalUseCase(
    private val repository: AccessRepository,
    private val database: GeneralDatabase,
    private val singer: Singer
) {
    @Throws(Exception::class)
    fun doLogin(username: String, password: String): CommonFlow<LoginState> {
        val general = GeneralDB(database)
        val flow = flow {
            try {
                emit(LoginState.Handshake)

                val auth = Authorization(username, password)
                val person = singer.me(auth)
                emit(LoginState.Connected(person))

                repository.insert(username, password)
                val profileId = general.profileDao.insert(person)

                singer.setDefaultAuthorization(auth)

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
                DisciplinesProcessor(general, disciplines, semester, profileId, false).execute()

                emit(LoginState.Completed)
            } catch (error: Exception) {
                println(error)
                emit(LoginState.Failed(error))
            }
        }
        return CommonFlow(flow)
    }
}