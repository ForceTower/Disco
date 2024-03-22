//
//  DomainModuleAssembly.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 10/03/24.
//

import Foundation
import Swinject
import Club

class DomainModuleAssembly : Assembly {
    func assemble(container: Container) {
        let useCases = UseCases()
        
        container.register(AttestationUseCase.self) { resolver in
            AttestationUseCase(repository: resolver.resolve(AuthRepository.self)!)
        }
        
        container.register(LoginPortalUseCase.self) { _ in
            useCases.loginPortal
        }
        
        container.register(ConnectedUserUseCase.self) { _ in
            useCases.connectedUser
        }
        
        container.register(GetAllMessagesUseCase.self) { _ in
            useCases.allMessages
        }
        
        container.register(GetDisciplinesUseCase.self) { _ in
            useCases.getDisciplines
        }
        
        container.register(GetScheduleUseCase.self) { _ in
            useCases.getSchedule
        }
        
        container.register(ClassDataUseCase.self) { _ in
            useCases.classData
        }
        
        container.register(LocalSyncDataUseCase.self) { resolver in
            LocalSyncDataUseCase(sync: useCases.syncData, notifications: useCases.pendingNotifications)
        }
        
        container.register(SyncDataUseCase.self) { _ in
            useCases.syncData
        }
        
        container.register(ServiceAuthUseCase.self) { _ in
            useCases.serviceAuth
        }
        
        container.register(GetAccountUseCase.self) { _ in
            useCases.getServiceAccount
        }
        
        container.register(ScheduleBackgroundProcessingUseCase.self) { _ in
            ScheduleBackgroundProcessingUseCase()
        }
        
        container.register(GetBigTrayQuotaUseCase.self) { _ in
            useCases.getBigTrayQuota
        }
        
        container.register(LinkEmailUseCase.self) { _ in
            useCases.linkEmail
        }
        
        container.register(ManagePasskeysUseCase.self) { _ in
            useCases.managePasskeys
        }
        
        container.register(MessagingUseCase.self) { _ in
            MessagingUseCase(messaging: useCases.messagingToken)
        }
    }
}
