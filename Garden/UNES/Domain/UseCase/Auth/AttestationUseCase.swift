//
//  AttestationUseCase.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 10/03/24.
//

import Foundation

class AttestationUseCase {
    private let repository: AuthRepository
    
    init(repository: AuthRepository) {
        self.repository = repository
    }
    
    func start() async throws -> AssertionStartData {
        return try await repository.attestationStart()
    }
}
