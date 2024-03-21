package dev.forcetower.unes.club.domain.usecase

import dev.forcetower.unes.club.domain.usecase.account.GetAccountUseCase
import dev.forcetower.unes.club.domain.usecase.account.LinkEmailUseCase
import dev.forcetower.unes.club.domain.usecase.account.ManagePasskeysUseCase
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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UseCases : KoinComponent {
    val loginPortal by inject<LoginPortalUseCase>()
    val connectedUser by inject<ConnectedUserUseCase>()
    val allMessages by inject<GetAllMessagesUseCase>()
    val getDisciplines by inject<GetDisciplinesUseCase>()
    val getSchedule by inject<GetScheduleUseCase>()
    val classData by inject<ClassDataUseCase>()
    val syncData by inject<SyncDataUseCase>()
    val pendingNotifications by inject<PendingNotificationsUseCase>()
    val getBigTrayQuota by inject<GetBigTrayQuotaUseCase>()
    val serviceAuth by inject<ServiceAuthUseCase>()
    val getServiceAccount by inject<GetAccountUseCase>()
    val linkEmail by inject<LinkEmailUseCase>()
    val managePasskeys by inject<ManagePasskeysUseCase>()
}