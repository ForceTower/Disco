//
//  AuthRepository.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 10/03/24.
//

import Foundation
import Combine

class AuthRepository {
    private let service: AuthService
    
    init(service: AuthService) {
        self.service = service
    }
    
    func attestationStart() async throws -> AssertionStartData {
        return try await service.startAttestation()
    }
}
