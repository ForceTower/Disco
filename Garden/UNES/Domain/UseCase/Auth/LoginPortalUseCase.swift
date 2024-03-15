//
//  LoginPortalUseCase.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 10/03/24.
//

import Arcadia
import Club

class LoginPortalUseCase {
    
    func login(username: String, password: String) -> AsyncThrowingStream<PortalLoginStatus, Error> {
        let arcadia = Arcadia(username: username, password: password)
        
        return AsyncThrowingStream<PortalLoginStatus, Error> { continuation in
            Task {
                do {
                    let person = try await arcadia.login().get()
                    continuation.yield(.fetchedUser(person: person))
                    
                    try await KMMUseCases().insertAccessUseCase.invoke(username: username, password: password)
                    let access = try await KMMUseCases().insertAccessUseCase.require()
                    print("Finished. \(String(describing: access?.username)) \(String(describing: access?.id))")
                    
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
