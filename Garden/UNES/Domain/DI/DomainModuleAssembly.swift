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
        container.register(AttestationUseCase.self) { resolver in
            AttestationUseCase(repository: resolver.resolve(AuthRepository.self)!)
        }
        
        container.register(LoginPortalUseCase.self) { _ in
            KMMUseCases().loginUseCase
        }
    }
}
