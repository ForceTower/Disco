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
}
