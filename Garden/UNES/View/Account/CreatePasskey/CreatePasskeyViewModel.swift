//
//  CreatePasskeyViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 20/03/24.
//

import SwiftUI
import Combine
import Club
import KMPNativeCoroutinesCombine
import KMPNativeCoroutinesAsync
import AuthenticationServices
import FirebaseCrashlytics

@available(iOS 16.4, *)
class CreatePasskeyViewModel : NSObject, ObservableObject, ASAuthorizationControllerDelegate {
    private let passkey: ManagePasskeysUseCase
    private var subscriptions = Set<AnyCancellable>()
    @Published var showError = false
    @Published private(set) var messageError = ""
    @Published var completed = false
    
    init(passkey: ManagePasskeysUseCase = AppDIContainer.shared.resolve()) {
        self.passkey = passkey
    }
    
    @Published private(set) var loading = false
    
    func startPasskeyCreate(auth: AuthorizationController) async {
        toggleLoading(true)
        do {
            let data = try await asyncFunction(for: passkey.registerStart())
            await onChallengeReceived(data, controller: auth)
        } catch {
            print("Failed to register - Network error: \(error.localizedDescription)")
            Crashlytics.crashlytics().record(error: error)
            onUnknownPasskeyEvent("Falhou ao buscar dados para criação")
        }

    }
    
    private func onChallengeReceived(_ data: RegisterPasskeyFlowStart, controller: AuthorizationController) async {
        do {
            let publicKey = data.register_.publicKey
            
            let platformProvider = ASAuthorizationPlatformPublicKeyCredentialProvider(relyingPartyIdentifier: publicKey.rp.id)
            print("Original challenge: \(publicKey.challenge)")
            print("Original challenge: \(String(describing: publicKey.user))")
            let platformKeyRequest = platformProvider.createCredentialRegistrationRequest(
                challenge: Data(base64Encoded: publicKey.challenge.thingy.fixedBase64Format)!,
                name: publicKey.user.name,
                userID: Data(publicKey.user.id.utf8))
            
            platformKeyRequest.displayName = publicKey.user.displayName
            platformKeyRequest.userVerificationPreference = .required
            
            let result = try await controller.performRequest(platformKeyRequest)
            await onRegistrationResult(result, flowId: data.flowId)
        } catch let authorizationError as ASAuthorizationError where authorizationError.code == .canceled {
            print("Registration cancelled")
            toggleLoading(false)
        } catch let authorizationError as ASAuthorizationError {
            print("Passkey registration failed. Error: \(authorizationError.localizedDescription)")
            onUnknownPasskeyEvent("Falhou ao registrar chave... \(authorizationError.localizedDescription)")
        } catch AuthorizationHandlingError.unknownAuthorizationResult(let authorizationResult) {
            print("""
            Passkey registration handling failed. \
            Received an unknown result: \(String(describing: authorizationResult))
            """)
            onUnknownPasskeyEvent("Ocorreu um erro desconhecido ao registrar chave.")
        } catch {
            print("""
            Passkey registration handling failed. \
            Caught an unknown error during passkey registration or handling: \(error.localizedDescription).
            """)
            
            onUnknownPasskeyEvent("Ocorreu um erro fora do comum. \(error.localizedDescription)")
        }
    }
    
    private func onRegistrationResult(_ result: ASAuthorizationResult, flowId: String) async {
        switch result {
        case let .passkeyRegistration(passkeyRegistration):
            print("Passkey registration succeeded: \(passkeyRegistration)")
            await sendKeyToServer(passkeyRegistration, flowId: flowId)
        default:
            onUnknownPasskeyEvent("Autorização inesperada")
        }
    }
    
    private func sendKeyToServer(_ registration: ASAuthorizationPlatformPublicKeyCredentialRegistration, flowId: String) async {
        
        guard let attestationObject = registration.rawAttestationObject else {
            onUnknownPasskeyEvent("Erro ao processar dados do cliente...")
            return
        }
        let clientDataJSON = registration.rawClientDataJSON
        let credentialID = registration.credentialID
        
        let payload = [
            "rawId": credentialID.base64URLEncodePadded(),
            "id": registration.credentialID.base64URLEncode(),
            "authenticatorAttachment": "platform",
            "clientExtensionResults": [
                "credProps": [
                    "rk": true
                ]
            ],
            "type": "public-key",
            "response": [
                "attestationObject": attestationObject.base64URLEncodePadded(),
                "clientDataJSON": clientDataJSON.base64URLEncodePadded()
            ]
        ] as [String: Any]
        
        print("Attest \(attestationObject.base64URLEncodePadded())")
        
        guard let payloadJSONData = try? JSONSerialization.data(withJSONObject: payload, options: [.withoutEscapingSlashes]),
              let payloadJSONText = String(data: payloadJSONData, encoding: .utf8) else {
                  onUnknownPasskeyEvent("Erro ao processar dados da chave...")
                  return
        }
        print("Result \(payloadJSONText)")
        do {
            let _ = try await asyncFunction(for: passkey.registerFinish(flowId: flowId, data: payloadJSONText))
            toggleLoading(false)
            toggleCompleted(true)
        } catch {
            onUnknownPasskeyEvent("Ocorreu um erro ao salvar senha no servidor... \(String(describing: error))")
        }
    }
    
    private func onUnknownPasskeyEvent(_ text: String) {
        DispatchQueue.main.async { [weak self] in
            self?.showError = true
            self?.messageError = text
            self?.loading = false
        }
    }
    
    private func toggleLoading(_ value: Bool) {
        DispatchQueue.main.async { [weak self] in
            self?.loading = value
        }
    }
    
    private func toggleCompleted(_ value: Bool) {
        DispatchQueue.main.async { [weak self] in
            self?.completed = value
        }
    }
}
