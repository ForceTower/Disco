//
//  AuthPortalLoginViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 09/03/24.
//

import SwiftUI
import AuthenticationServices
import Combine
import KMPNativeCoroutinesCombine
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
    
    var router: RootRouter? = nil
    
    @Published private(set) var name: String? = nil
    @Published private(set) var loading: Bool = false
    @Published var showLoginError: Bool = false
    var errorTitle = ""
    var errorDescription = ""
    
    private var subscriptions = Set<AnyCancellable>()
    
    init(
        attestationUseCase: AttestationUseCase,
        loginUseCase: LoginPortalUseCase
    ) {
        self.attestationUseCase = attestationUseCase
        self.loginUseCase = loginUseCase
    }
    
    func login(username: String, password: String) {
        if loading { return }
        loading = true
        createPublisher(for: loginUseCase.doLogin(username: username, password: password))
            .receive(on: DispatchQueue.main)
            .sink { [weak self] completion in
                self?.loading = false
                switch completion {
                case .finished:
                    self?.onLoginOpFinished()
                case .failure(let error):
                    self?.onLoginError(error)
                }
                print("Finished.")
            } receiveValue: { [weak self] value in
                switch value {
                case let connected as LoginState.Connected:
                    self?.name = connected.person.name
                case let fail as LoginState.LoginFailed:
                    self?.onLoginFailed(fail)
                case is LoginState.Completed:
                    self?.onLoginSuccess()
                default:
                    print("No-op")
                }
            }
            .store(in: &subscriptions)

    }
    
    func onLoginOpFinished() {
        print("Login op finished.")
    }
    
    func onLoginSuccess() {
        router?.state = .connected
    }
    
    func onLoginError(_ error: Error) {
        print(error.localizedDescription)
        errorTitle = "Falha de conexão"
        errorDescription = "Ocorreu um erro de comunicação entre o UNES e o Portal... A internet está ruim ou o portal caiu de novo :)"
        showLoginError = true
    }
    
    func onLoginFailed(_ fail: LoginState.LoginFailed) {
        switch fail.reason {
        case .invalidcredentials:
            errorTitle = "Credenciais inválidas"
            errorDescription = "Verifique os dados informados e tente novamente"
        case .developererror:
            errorTitle = "Falha de processamento"
            errorDescription = "Algo esquisito fez com que o login parasse. Este erro precisa ser investigado"
        default:
            errorTitle = "Erro desconhecido"
            errorDescription = "Eu não sei o que aconteceu. Mas foi algo completamente inesperado"
        }
        showLoginError = true
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
