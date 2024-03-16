//
//  LoginPortalUseCase.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 10/03/24.
//

import Club

class LoginPortalUseCase {
    
    func login(username: String, password: String) -> AsyncThrowingStream<PortalLoginStatus, Error> {
        let usecase = KMMUseCases().insertAccessUseCase
        
        return AsyncThrowingStream<PortalLoginStatus, Error> { continuation in
            Task {
                do {
                    let person = try await usecase.me(username: username, password: password)
                    usecase.setSingerAuth(auth: SingerSingerAuthorization(username: username, password: password))
                    let semesters = try await usecase.semesters(id: person.id)
                    print(String(describing: semesters))
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
