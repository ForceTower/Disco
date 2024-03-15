package dev.forcetower.unes.club.domain.usecase

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class KMMUseCases : KoinComponent {
    val insertAccessUseCase by inject<InsertAccessUseCase>()
}