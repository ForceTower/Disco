package dev.forcetower.unes.club.domain.usecase.auth

import dev.forcetower.unes.club.data.processor.MessagesProcessor
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
    fun doLogin(username: String, password: String): CommonFlow<LoginState> {
        val flow = flow {
            try {
                emit(LoginState.Handshake)

                val auth = Authorization(username, password)
                val person = singer.me(auth)
                emit(LoginState.Connected(person))

                singer.setDefaultAuthorization(auth)

                val messages = singer.messages(person.id)
                MessagesProcessor(messages, database, true).execute()
                emit(LoginState.Messages)

                val semesters = singer.semesters(person.id)


                emit(LoginState.Grades)
            } catch (error: Exception) {
                println(error)
                emit(LoginState.Failed(error))
            }
        }
        return CommonFlow(flow)
    }
}