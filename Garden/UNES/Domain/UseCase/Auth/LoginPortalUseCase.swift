//
//  LoginPortalUseCase.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 10/03/24.
//

import Arcadia

class LoginPortalUseCase {
    
    func login(username: String, password: String) -> AsyncThrowingStream<PortalLoginStatus, Error> {
        let arcadia = Arcadia(username: username, password: password)
        
        return AsyncThrowingStream<PortalLoginStatus, Error> { continuation in
            Task {
                do {
                    let person = try await arcadia.login().get()
                    continuation.yield(.fetchedUser(person: person))
                } catch {
                    print("Failed with error \(error.localizedDescription)")
                    print(error)
//                    Crashlytics.crashlytics().log("Failed to run login")
//                    Crashlytics.crashlytics().log(String(describing: error))
//                    Crashlytics.crashlytics().record(error: error)
                    continuation.finish(throwing: error)
                }
            }
            continuation.yield(.handshake)
        }
    }
}
