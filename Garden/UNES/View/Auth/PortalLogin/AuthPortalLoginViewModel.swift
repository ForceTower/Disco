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
        loginUseCase: LoginPortalUseCase = AppDIContainer.shared.resolve(),
        serviceAuthUseCase: ServiceAuthUseCase = AppDIContainer.shared.resolve()
    ) {
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
            } receiveValue: { [weak self] value in
                switch value {
                case let connected as LoginState.Connected:
                    self?.name = connected.person.name
                case let fail as LoginState.LoginFailed:
                    self?.onLoginFailed(fail)
                case is LoginState.Completed:
                    self?.onLoginSuccess()
                default:
                    Logger.authorization.debug("Login step: \(value)")
                }
            }
            .store(in: &subscriptions)

    }
    
    func onLoginOpFinished() {
        Logger.authorization.info("Login completed")
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
        toggleLoading(true)
        do {
            let data = try await asyncFunction(for: serviceAuthUseCase.startAssertion())
            await requestAttestation(controller: controller, data: data)
        } catch {
            onPasskeyError("Erro ao buscar dados do servidor")
        }
    }
    
    @available(iOS 16.4, *)
    private func requestAttestation(controller: AuthorizationController, data: PasskeyAssertionData) async {
        let publicKey = data.challenge.publicKey
        let challenge = Data(base64Encoded: publicKey.challenge.thingy.fixedBase64Format)!
        let platformProvider = ASAuthorizationPlatformPublicKeyCredentialProvider(relyingPartyIdentifier: publicKey.rpId)
        let platformKeyRequest = platformProvider.createCredentialAssertionRequest(challenge: challenge)
        platformKeyRequest.userVerificationPreference = .required
        
        do {
            let response = try await controller.performRequest(platformKeyRequest)
            await handleResponse(controller: controller, authorization: response, flowId: data.flowId)
        } catch let error as ASAuthorizationError where error.code == .canceled {
            toggleLoading(false)
            Logger.authorization.debug("The user cancelled passkey authorization.")
        } catch let error as ASAuthorizationError {
            onPasskeyError("Erro ao autorizar chave de acesso")
            Logger.authorization.error("Passkey authorization failed. Error: \(error.localizedDescription)")
        } catch let AuthorizationHandlingError.unknownAuthorizationResult(authorizationResult) {
            onPasskeyError("Erro desconhecido ao buscar chaves. Tente novamente mais tarde")
            Logger.authorization.error("""
            Passkey authorization handling failed. \
            Received an unknown result: \(String(describing: authorizationResult))
            """)
        } catch {
            onPasskeyError("Erro desconhecido durante autorização. Tente novamente depois")
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
            toggleLoading(false)
            Logger.authorization.error("Invalid type received during attestation")
        }
    }
    
    func completeAssertion(assertion: ASAuthorizationPublicKeyCredentialAssertion, flowId: String) async {
        let clientDataJSON = assertion.rawClientDataJSON
        let credentialID = assertion.credentialID
        
        guard let userHandle = String(data: assertion.userID, encoding: .utf8) else {
            onPasskeyError("Não foi possível recuperar o ID de usuário")
            return
        }
        
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
            onPasskeyError("Erro de conversão da chave.")
            return
        }
        
        print("Result \(payloadJSONText)")
        
        do {
            let _ = try await asyncFunction(for: serviceAuthUseCase.finishAssertion(flowId: flowId, credential: payloadJSONText))
            toggleLoading(false)
        } catch {
            print("Failed with \(error.localizedDescription)")
            onPasskeyError("Erro de comunicação ao autenticar chave.")
        }
    }
    
    private func onPasskeyError(_ message: String) {
        DispatchQueue.main.async { [weak self] in
            self?.loading = false
            self?.errorDescription = message
        }
    }
    
    private func toggleLoading(_ value: Bool) {
        DispatchQueue.main.async { [weak self] in
            self?.loading = value
        }
    }
}
