//
//  AccountViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 19/03/24.
//

import Combine
import Club
import KMPNativeCoroutinesCombine

class AccountViewModel : ObservableObject {
    private let account: GetAccountUseCase
    private let user: ConnectedUserUseCase
    
    private var subscriptions = Set<AnyCancellable>()
    @Published private(set) var currentProfile: Profile? = nil
    @Published private(set) var currentAccount: ServiceAccount? = nil
    
    init(user: ConnectedUserUseCase = AppDIContainer.shared.resolve(),
         account: GetAccountUseCase = AppDIContainer.shared.resolve()) {
        self.user = user
        self.account = account
        fetchProfile()
        fetchAccount()
    }
    
    private func fetchProfile() {
        createPublisher(for: user.currentProfile())
            .receive(on: DispatchQueue.main)
            .sink { _ in
                
            } receiveValue: { [weak self] profile in
                self?.currentProfile = profile
            }
            .store(in: &subscriptions)
    }
    
    private func fetchAccount() {
        createPublisher(for: account.getAccount())
            .receive(on: DispatchQueue.main)
            .sink { completion in
                print("Received flow account completion \(completion)")
            } receiveValue: { [weak self] account in
                self?.currentAccount = account
            }
            .store(in: &subscriptions)
        
        createFuture(for: account.fetchAccount())
            .receive(on: DispatchQueue.main)
            .sink { completion in
                print("Fetch account completion \(completion)")
            } receiveValue: { [weak self] account in
                self?.currentAccount = account
            }
            .store(in: &subscriptions)
    }
}
