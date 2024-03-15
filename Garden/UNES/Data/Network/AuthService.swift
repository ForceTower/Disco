//
//  AuthService.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 10/03/24.
//

import Foundation
import Combine

class AuthService : BaseService {
    func startAttestation() async throws -> AssertionStartData {
        let url = URL(string: "\(baseUrl)auth/login/passkey/assertion/start")!
        let (data, _) = try await URLSession.shared.data(from: url)
        let value = try JSONDecoder().decode(AssertionStartData.self, from: data)
        return value
    }
}
