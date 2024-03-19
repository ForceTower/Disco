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
    private let user: ConnectedUserUseCase
    private var subscriptions = Set<AnyCancellable>()
    @Published private(set) var currentProfile: Profile? = nil
    
    init(user: ConnectedUserUseCase = AppDIContainer.shared.resolve()) {
        self.user = user
        fetchProfile()
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
}
