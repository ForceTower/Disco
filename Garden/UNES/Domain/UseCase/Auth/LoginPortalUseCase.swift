//
//  LoginPortalUseCase.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 10/03/24.
//

import Club

class LoginPortalUseCase {
    
    func login(username: String, password: String) -> AsyncThrowingStream<PortalLoginStatus, Error> {
        let usecase = KMMUseCases().loginUseCase
        
        return AsyncThrowingStream<PortalLoginStatus, Error> { continuation in
            Task {
                do {
                    usecase.doLogin(username: username, password: password).subscribe { state in
                        switch state {
                        case let connected as LoginState.Connected:
                            print("Person \(connected.person)")
                        case is LoginState.Handshake:
                            print("Handshake")
                        case is LoginState.Grades:
                            print("Grades")
                        case is LoginState.Messages:
                            print("Messages")
                        case is LoginState.Semesters:
                            print("Semesters")
                        case let failed as LoginState.Failed:
                            print("Failed :( \(failed)")
                        default:
                            print("Unknown state :)")
                        }
                    }
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
