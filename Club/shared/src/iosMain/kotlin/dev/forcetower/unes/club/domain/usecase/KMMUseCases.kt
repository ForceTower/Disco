package dev.forcetower.unes.club.domain.usecase

import dev.forcetower.unes.club.domain.usecase.auth.LoginPortalUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class KMMUseCases : KoinComponent {
    val loginUseCase by inject<LoginPortalUseCase>()
    val insertAccessUseCase by inject<InsertAccessUseCase>()
}