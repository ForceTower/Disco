package dev.forcetower.unes.club.domain.di

import dev.forcetower.unes.club.domain.usecase.account.GetAccountUseCase
import dev.forcetower.unes.club.domain.usecase.account.LinkEmailUseCase
import dev.forcetower.unes.club.domain.usecase.account.ManagePasskeysUseCase
import dev.forcetower.unes.club.domain.usecase.account.MessagingTokenUseCase
import dev.forcetower.unes.club.domain.usecase.auth.ConnectedUserUseCase
import dev.forcetower.unes.club.domain.usecase.auth.LoginPortalUseCase
import dev.forcetower.unes.club.domain.usecase.auth.ServiceAuthUseCase
import dev.forcetower.unes.club.domain.usecase.bigtray.GetBigTrayQuotaUseCase
import dev.forcetower.unes.club.domain.usecase.classes.ClassDataUseCase
import dev.forcetower.unes.club.domain.usecase.disciplines.GetDisciplinesUseCase
import dev.forcetower.unes.club.domain.usecase.messages.GetAllMessagesUseCase
import dev.forcetower.unes.club.domain.usecase.notification.PendingNotificationsUseCase
import dev.forcetower.unes.club.domain.usecase.schedule.GetScheduleUseCase
import dev.forcetower.unes.club.domain.usecase.sync.SyncDataUseCase
import org.koin.dsl.module

object DomainDI {
    val useCase = module {
        factory { LoginPortalUseCase(get(), get(), get(), get(), get()) }
        factory { ConnectedUserUseCase(get(), get()) }
        factory { GetAllMessagesUseCase(get()) }
        factory { GetDisciplinesUseCase(get()) }
        factory { GetScheduleUseCase(get()) }
        factory { ClassDataUseCase(get()) }
        factory { SyncDataUseCase(get(), get()) }
        factory { PendingNotificationsUseCase(get(), get()) }
        factory { GetBigTrayQuotaUseCase(get()) }
        factory { ServiceAuthUseCase(get(), get()) }
        factory { GetAccountUseCase(get()) }
        factory { LinkEmailUseCase(get()) }
        factory { ManagePasskeysUseCase(get()) }
        factory { MessagingTokenUseCase(get()) }
    }
}