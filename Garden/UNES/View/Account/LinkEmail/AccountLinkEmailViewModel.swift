//
//  AccountLinkEmailViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 20/03/24.
//

import Combine
import Club
import KMPNativeCoroutinesCombine

class AccountLinkEmailViewModel : ObservableObject {
    private let linkEmail: LinkEmailUseCase
    private var subscriptions = Set<AnyCancellable>()
    @Published private(set) var loading = false
    @Published var securityCode = ""
    
    init(email: LinkEmailUseCase = AppDIContainer.shared.resolve()) {
        self.linkEmail = email
    }
    
    func register(email: String) {
        if loading { return }
        loading = true
        createFuture(for: linkEmail.registerEmail(email: email))
            .receive(on: DispatchQueue.main)
            .sink { [weak self] completion in
                self?.loading = false
                print("Received completion \(completion)")
                switch completion {
                case .finished:
                    print("Finished ok")
                case .failure(let error):
                    self?.onLinkFailed(error)
                }
            } receiveValue: { [weak self] value in
                self?.onCompleteStep(value)
            }
            .store(in: &subscriptions)
    }
    
    private func onCompleteStep(_ value: String) {
        securityCode = value
    }
    
    private func onLinkFailed(_ error: Error) {
        
    }
    
}
