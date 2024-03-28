//
//  AccountHandshakeViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 20/03/24.
//

import Combine
import Club
import FirebaseCrashlytics
import KMPNativeCoroutinesCombine
import KMPNativeCoroutinesAsync
import AuthenticationServices
import SwiftUI

class AccountHandshakeViewModel : ObservableObject {
    private let authUseCase: ServiceAuthUseCase
    private var subscriptions = Set<AnyCancellable>()
    
    @Published private(set) var loading = false
    @Published var showError = false
    @Published var titleError = ""
    @Published var messageError = ""
    @Published var completed = false
    @Published var finished = false
    
    init(authUseCase: ServiceAuthUseCase = AppDIContainer.shared.resolve()) {
        self.authUseCase = authUseCase
    }
    
    func handshake() {
        if loading { return }
        loading = true
        createFuture(for: authUseCase.handshake())
            .receive(on: DispatchQueue.main)
            .sink { [weak self] completion in
                self?.loading = false
                print("Received completion")
                switch completion {
                case .finished:
                    print("Finished ok")
                case .failure(let error):
                    self?.onHandshakeFatal(error)
                }
            } receiveValue: { [weak self] value in
                self?.onCompleteHandshake(value)
            }
            .store(in: &subscriptions)
    }
    
    @available(iOS 16.4, *)
    func loginWithPasskey(controller: AuthorizationController) async {
        toggleLoading(true)
        do {
            let data = try await asyncFunction(for: authUseCase.startAssertion())
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
        } catch _ as ASAuthorizationError {
            onPasskeyError("Erro ao autorizar chave de acesso")
        } catch AuthorizationHandlingError.unknownAuthorizationResult(_) {
            onPasskeyError("Erro desconhecido ao buscar chaves. Tente novamente mais tarde")
        } catch {
            onPasskeyError("Erro desconhecido durante autorização. Tente novamente depois")
        }
    }
    
    @available(iOS 16.4, *)
    func handleResponse(controller: AuthorizationController, authorization: ASAuthorizationResult, flowId: String) async {
        switch authorization {
        case let .passkeyAssertion(passkeyAssertion):
            await completeAssertion(assertion: passkeyAssertion, flowId: flowId)
        default:
            toggleLoading(false)
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
            let result = try await asyncFunction(for: authUseCase.finishAssertion(flowId: flowId, credential: payloadJSONText))
            DispatchQueue.main.async { [weak self] in
                self?.onPasskeyCompleted(result)
            }
            toggleLoading(false)
        } catch {
            print("Failed with \(error.localizedDescription)")
            onPasskeyError("Erro de comunicação ao autenticar chave.")
        }
    }
    
    private func onCompleteHandshake(_ value: ServiceAuthResult) {
        print("Received handshake result \(value)")
        switch value {
        case let connected as ServiceAuthResult.Connected:
            onHandshakeCompleted(connected)
        case is ServiceAuthResult.RejectedCredential:
            onRejectedCredentials()
        case is ServiceAuthResult.MissingCredential:
            onRejectedCredentials()
        case let fail as ServiceAuthResult.ConnectionFailed:
            onConnectionFailed(fail.reason)
        case let fail as ServiceAuthResult.UnknownError:
            onHandshakeFailed(fail.reason)
        default:
            onUnknownError()
        }
    }
    
    private func onHandshakeCompleted(_ connected: ServiceAuthResult.Connected) {
        if let email = connected.account?.email, !email.isEmpty {
            finished = true
        } else {
            completed = true
        }
    }
    
    private func onPasskeyCompleted(_ account: ServiceAccount) {
        if let email = account.email, !email.isEmpty {
            finished = true
        } else {
            completed = true
        }
    }
    
    private func onRejectedCredentials() {
        titleError = "Credenciais inválidas"
        messageError = "O UNES não conseguiu validar seu acesso. Tente sair da sua conta e fazer login novamente"
        showError = true
    }
    
    private func onConnectionFailed(_ reason: String) {
        titleError = "Erro de conexão"
        messageError = "Não foi possível efetuar a operação. Erro: \(reason)"
        showError = true
    }
    
    private func onHandshakeFailed(_ reason: String) {
        titleError = "Erro de processamento"
        messageError = "Não foi possível completar a operação. Erro: \(reason)"
        showError = true
    }
    
    private func onUnknownError() {
        titleError = "????"
        messageError = "Este erro não deveria ter acontecido..."
        showError = true
    }
    
    private func onHandshakeFatal(_ error: Error) {
        print("Failed to run handshake with: \(error.localizedDescription)")
        Crashlytics.crashlytics().log("Failed to run handshake with: \(error.localizedDescription)")
        Crashlytics.crashlytics().record(error: error)
    }
    
    private func onPasskeyError(_ message: String) {
        DispatchQueue.main.async { [weak self] in
            self?.loading = false
            self?.titleError = "Erro na autenticação"
            self?.messageError = message
            self?.showError = true
        }
    }
    
    private func toggleLoading(_ value: Bool) {
        DispatchQueue.main.async { [weak self] in
            self?.loading = value
        }
    }
}
