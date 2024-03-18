//
//  BigTrayViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 18/03/24.
//

import Club
import Combine
import KMPNativeCoroutinesCombine

class BigTrayViewModel : ObservableObject {
    private let getQuota: GetBigTrayQuotaUseCase
    private var subscriptions = Set<AnyCancellable>()
    
    @Published private(set) var data: BigTrayData? = nil
    
    init(getQuota: GetBigTrayQuotaUseCase = AppDIContainer.shared.resolve()) {
        self.getQuota = getQuota
        fetchQuota()
    }
    
    private func fetchQuota() {
        createPublisher(for: getQuota.quota(delayMs: 10000))
            .receive(on: DispatchQueue.main)
            .sink { _ in
                
            } receiveValue: { [weak self] data in
                self?.data = data
            }
            .store(in: &subscriptions)

    }
}
