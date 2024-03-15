//
//  DataModuleAssembly.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 10/03/24.
//

import Foundation
import Swinject

class DataModuleAssembly : Assembly {
    func assemble(container: Container) {
        container.register(AuthService.self) { _ in
            AuthService()
        }.inObjectScope(.container)
        
        container.register(AuthRepository.self) { resolver in
            AuthRepository(service: resolver.resolve(AuthService.self)!)
        }.inObjectScope(.container)
    }
}
