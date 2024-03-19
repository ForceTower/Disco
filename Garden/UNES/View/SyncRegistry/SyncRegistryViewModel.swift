//
//  SyncRegistryViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 19/03/24.
//

import Combine
import Club
import KMPNativeCoroutinesCombine

class SyncRegistryViewModel : ObservableObject {
    private let sync: SyncDataUseCase
    private var subscriptions = Set<AnyCancellable>()
    @Published private(set) var elements: [SyncRegistry] = []
    
    init(sync: SyncDataUseCase = AppDIContainer.shared.resolve()) {
        self.sync = sync
        fetchData()
    }
    
    private func fetchData() {
        createPublisher(for: sync.registry())
            .receive(on: DispatchQueue.main)
            .sink { _ in
                
            } receiveValue: { [weak self] data in
                self?.elements = data
            }
            .store(in: &subscriptions)
    }
}
