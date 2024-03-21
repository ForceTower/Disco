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
import KMPNativeCoroutinesAsync
import os
import Club

public extension Logger {
    static let authorization = Logger(subsystem: "UNES", category: "Authentication")
}

@available(iOS 16.4, *)
public enum AuthorizationHandlingError: Error {
    case unknownAuthorizationResult(ASAuthorizationResult)
    case otherError
}

class AuthPortalLoginViewModel : NSObject, ObservableObject, ASAuthorizationControllerDelegate {
    private let attestationUseCase: AttestationUseCase
    private let loginUseCase: LoginPortalUseCase
    private let serviceAuthUseCase: ServiceAuthUseCase
    
    var router: RootRouter? = nil
    
    @Published private(set) var name: String? = nil
    @Published private(set) var loading: Bool = false
    @Published var showLoginError: Bool = false
    var errorTitle = ""
    var errorDescription = ""
    
    private var subscriptions = Set<AnyCancellable>()
    
    init(
        attestationUseCase: AttestationUseCase = AppDIContainer.shared.resolve(),
        loginUseCase: LoginPortalUseCase = AppDIContainer.shared.resolve(),
        serviceAuthUseCase: ServiceAuthUseCase = AppDIContainer.shared.resolve()
    ) {
        self.attestationUseCase = attestationUseCase
        self.loginUseCase = loginUseCase
        self.serviceAuthUseCase = serviceAuthUseCase
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
    
    @available(iOS 16.4, *)
    func startAttestation(controller: AuthorizationController) async {
        do {
            let data = try await attestationUseCase.start()
            await requestAttestation(controller: controller, data: data)
        } catch {
            print(error)
        }
    }
    
    @available(iOS 16.4, *)
    private func requestAttestation(controller: AuthorizationController, data: AssertionStartData) async {
        let publicKey = data.challenge.publicKey
        print("Initial challenge: \(publicKey.challenge)")
        let challenge = Data(base64Encoded: publicKey.challenge.thingy.fixedBase64Format)!
        let platformProvider = ASAuthorizationPlatformPublicKeyCredentialProvider(relyingPartyIdentifier: publicKey.rpId)
        let platformKeyRequest = platformProvider.createCredentialAssertionRequest(challenge: challenge)
        platformKeyRequest.userVerificationPreference = .required
        
        do {
            let response = try await controller.performRequest(platformKeyRequest)
            await handleResponse(controller: controller, authorization: response, flowId: data.flowId)
        } catch let error as ASAuthorizationError where error.code == .canceled {
            Logger.authorization.log("The user cancelled passkey authorization.")
        } catch let error as ASAuthorizationError {
            DispatchQueue.main.async { [weak self] in
                self?.errorTitle = "Passkey err"
                self?.errorDescription = "Passkey authorization failed. Error: \(error.localizedDescription)"
                self?.showLoginError = true
            }
            Logger.authorization.error("Passkey authorization failed. Error: \(error.localizedDescription)")
        } catch let AuthorizationHandlingError.unknownAuthorizationResult(authorizationResult) {
            DispatchQueue.main.async { [weak self] in
                self?.errorTitle = "Passkey err"
                self?.errorDescription = """
                Passkey authorization handling failed. \
                Received an unknown result: \(String(describing: authorizationResult))
                """
                self?.showLoginError = true
            }
            Logger.authorization.error("""
            Passkey authorization handling failed. \
            Received an unknown result: \(String(describing: authorizationResult))
            """)
        } catch {
            DispatchQueue.main.async { [weak self] in
                self?.errorTitle = "Passkey err"
                self?.errorDescription = """
                Passkey authorization handling failed. \
                Caught an unknown error during passkey authorization or handling: \(error.localizedDescription)"
                """
                self?.showLoginError = true
            }
            Logger.authorization.error("""
            Passkey authorization handling failed. \
            Caught an unknown error during passkey authorization or handling: \(error.localizedDescription)"
            """)
        }
    }
    
    @available(iOS 16.4, *)
    func handleResponse(controller: AuthorizationController, authorization: ASAuthorizationResult, flowId: String) async {
        switch authorization {
        case let .passkeyAssertion(passkeyAssertion):
            await completeAssertion(assertion: passkeyAssertion, flowId: flowId)
        default:
            Logger.authorization.error("Invalid type received during attestation")
        }
    }
    
    func completeAssertion(assertion: ASAuthorizationPublicKeyCredentialAssertion, flowId: String) async {
        let clientDataJSON = assertion.rawClientDataJSON
        let credentialID = assertion.credentialID
        
        guard let clientData = try? JSONSerialization.jsonObject(with: clientDataJSON) as? [String: Any] else {
            // onUnknownPasskeyEvent("Erro ao processar dados do cliente...")
            return
        }
        print("Weow! so good! \(clientData)")
        
        guard let userHandle = String(data: assertion.userID, encoding: .utf8) else { return }
        print("User id \(userHandle)")
        
        let payload = [
            "rawId": credentialID.base64URLEncodePadded(),
            "id": credentialID.base64URLEncode(),
            "authenticatorAttachment": "platform",
            "clientExtensionResults": [String:Any](),
            "type": "public-key",
            "response": [
                "signature": assertion.signature.base64URLEncodePadded(),
                "clientDataJSON": clientDataJSON.base64URLEncodePadded(),
                "authenticatorData": assertion.rawAuthenticatorData.base64URLEncodePadded(),
                "userHandle": userHandle
            ]
        ] as [String : Any]
        
        guard let payloadJSONData = try? JSONSerialization.data(withJSONObject: payload, options: [.withoutEscapingSlashes]),
              let payloadJSONText = String(data: payloadJSONData, encoding: .utf8) else {
            // onUnknownPasskeyEvent("Erro ao processar dados da chave...")
            return
        }
        
        print("Result \(payloadJSONText)")
        
        do {
            let account = try await asyncFunction(for: serviceAuthUseCase.finishAssertion(flowId: flowId, credential: payloadJSONText))
            print("Finished? \(account)")
            // toggleLoading(false)
            // toggleCompleted(true)
        } catch {
            print("Failed with \(error.localizedDescription)")
            // onUnknownPasskeyEvent("Ocorreu um erro ao salvar senha no servidor... \(String(describing: error))")
        }
    }
}
