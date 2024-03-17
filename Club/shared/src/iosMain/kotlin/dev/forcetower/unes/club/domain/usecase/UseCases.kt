package dev.forcetower.unes.club.domain.usecase

import dev.forcetower.unes.club.domain.usecase.auth.ConnectedUserUseCase
import dev.forcetower.unes.club.domain.usecase.auth.LoginPortalUseCase
import dev.forcetower.unes.club.domain.usecase.disciplines.GetDisciplinesUseCase
import dev.forcetower.unes.club.domain.usecase.messages.GetAllMessagesUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UseCases : KoinComponent {
    val loginPortal by inject<LoginPortalUseCase>()
    val connectedUser by inject<ConnectedUserUseCase>()
    val allMessages by inject<GetAllMessagesUseCase>()
    val getDisciplines by inject<GetDisciplinesUseCase>()
}