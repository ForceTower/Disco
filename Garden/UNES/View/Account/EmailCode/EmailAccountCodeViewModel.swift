//
//  EmailAccountCodeViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 20/03/24.
//

import Club
import Combine
import KMPNativeCoroutinesCombine

class EmailAccountCodeViewModel : ObservableObject {
    private let linkEmail: LinkEmailUseCase
    private var subscriptions = Set<AnyCancellable>()
    @Published private(set) var loading = false
    @Published private(set) var completed = false
    @Published private(set) var showResend = false
    @Published var showError = false
    @Published private(set) var errorTitle = ""
    @Published private(set) var errorSubtitle = ""
    
    private var countStarted = false
    
    init(email: LinkEmailUseCase = AppDIContainer.shared.resolve()) {
        self.linkEmail = email
    }
    
    func confirm(code: String, security: String) {
        if loading { return }
        loading = true
        
        createFuture(for: linkEmail.completeEmailRegister(code: code, security: security))
            .receive(on: DispatchQueue.main)
            .sink { [weak self] completion in
                print("Completed confirm")
                self?.loading = false
                switch completion {
                case .failure(let error):
                    self?.onLinkFailed(error)
                case .finished:
                    print("Done")
                }
            } receiveValue: { [weak self] value in
                self?.onLinkFinished(value)
            }
            .store(in: &subscriptions)
    }
    
    private func onLinkFinished(_ value: ServiceLinkEmailCompleteResult) {
        switch value {
        case is ServiceLinkEmailCompleteResult.Success:
            onLinkCompleted()
        case is ServiceLinkEmailCompleteResult.InvalidCode:
            onInvalidCode()
        case let error as ServiceLinkEmailCompleteResult.Error:
            onLinkFailed(other: error)
        default:
            print("Unmapped.")
        }
    }
    
    private func onLinkCompleted() {
        completed = true
    }
    
    private func onInvalidCode() {
        showError = true
        errorTitle = "Código inválido"
        errorSubtitle = "Verifique o código e tente novamente"
    }
    
    private func onLinkFailed(_ error: Error) {
        showError = true
        errorTitle = "Erro desconhecido..."
        errorSubtitle = "Algo esquisito aconteceu, tente novamente depois"
        
    }
    
    private func onLinkFailed(other error: ServiceLinkEmailCompleteResult.Error) {
        showError = true
        errorTitle = "Erro \(error.code)"
        errorSubtitle = "Algo esquisito aconteceu, tente novamente depois"
    }
    
    func startCount() {
        if countStarted { return }
        countStarted = true
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 30.0) { [weak self] in
            self?.showResend = true
        }
    }
}
