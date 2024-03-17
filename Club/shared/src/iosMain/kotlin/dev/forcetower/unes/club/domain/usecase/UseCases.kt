package dev.forcetower.unes.club.domain.usecase

import dev.forcetower.unes.club.domain.usecase.auth.ConnectedUserUseCase
import dev.forcetower.unes.club.domain.usecase.auth.LoginPortalUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UseCases : KoinComponent {
    val loginPortal by inject<LoginPortalUseCase>()
    val connectedUser by inject<ConnectedUserUseCase>()
}