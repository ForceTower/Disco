package dev.forcetower.unes.club.domain.model

import dev.forcetower.unes.singer.data.model.dto.Person

sealed class LoginState {
    data object Handshake : LoginState()
    data class Connected(val person: Person) : LoginState()
    data object Messages : LoginState()
    data object Semesters : LoginState()
    data object Grades : LoginState()
    data class Failed(val error: Exception) : LoginState()
}