//
//  AuthPortalLoginViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 09/03/24.
//

import SwiftUI
import AuthenticationServices
import Combine
import os
import Club

public extension Logger {
    static let authorization = Logger(subsystem: "UNES", category: "Authentication")
}

public enum AuthorizationHandlingError: Error {
    case unknownAuthorizationResult(ASAuthorizationResult)
    case otherError
}

class AuthPortalLoginViewModel : NSObject, ObservableObject, ASAuthorizationControllerDelegate {
    private let attestationUseCase: AttestationUseCase
    private let loginUseCase: LoginPortalUseCase
    
    @Published private(set) var name: String? = nil
    
    private var subscriptions = Set<AnyCancellable>()
    
    init(attestationUseCase: AttestationUseCase, loginUseCase: LoginPortalUseCase) {
        self.attestationUseCase = attestationUseCase
        self.loginUseCase = loginUseCase
    }
    
    func login(username: String, password: String) {
        let subject = loginUseCase.login(username: username, password: password)
        PassthroughSubject.emittingValues(from: subject)
            .receive(on: DispatchQueue.main)
            .sink { completion in
                
            } receiveValue: { status in
                
            }
            .store(in: &subscriptions)

    }
    
    func startAttestation(controller: AuthorizationController) async {
        do {
            let data = try await attestationUseCase.start()
            await requestAttestation(controller: controller, data: data)
        } catch {
            print(error)
        }
    }
    
    private func requestAttestation(controller: AuthorizationController, data: AssertionStartData) async {
        let challenge = Data(data.challenge.publicKey.challenge.utf8)
        let platformProvider = ASAuthorizationPlatformPublicKeyCredentialProvider(relyingPartyIdentifier: "edge-unes.forcetower.dev")
        let platformKeyRequest = platformProvider.createCredentialAssertionRequest(challenge: challenge)
        
        do {
            let response = try await controller.performRequest(platformKeyRequest)
            handleResponse(controller: controller, authorization: response)
        } catch let error as ASAuthorizationError where error.code == .canceled {
            Logger.authorization.log("The user cancelled passkey authorization.")
        } catch let error as ASAuthorizationError {
            Logger.authorization.error("Passkey authorization failed. Error: \(error.localizedDescription)")
        } catch let AuthorizationHandlingError.unknownAuthorizationResult(authorizationResult) {
            Logger.authorization.error("""
            Passkey authorization handling failed. \
            Received an unknown result: \(String(describing: authorizationResult))
            """)
        } catch {
            Logger.authorization.error("""
            Passkey authorization handling failed. \
            Caught an unknown error during passkey authorization or handling: \(error.localizedDescription)"
            """)
        }
    }
    
    func handleResponse(controller: AuthorizationController, authorization: ASAuthorizationResult) {
        switch authorization {
        case let .passkeyAssertion(passkeyAssertion):
            guard let username = String(bytes: passkeyAssertion.userID, encoding: .utf8) else {
                fatalError("Invalid credential: \(passkeyAssertion)")
            }
            print(username)
        default:
            Logger.authorization.error("Invalid type received during attestation")
        }
    }
}
