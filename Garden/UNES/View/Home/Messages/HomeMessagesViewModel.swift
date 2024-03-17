//
//  HomeMessagesViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 16/03/24.
//

import Club
import Combine
import KMPNativeCoroutinesCombine

class HomeMessagesViewModel : ObservableObject {
    private let messagesUseCase: GetAllMessagesUseCase
    
    @Published var messages: [Message] = []
    
    private var subscriptions = Set<AnyCancellable>()
    private var observing = false
    
    init(messagesUseCase: GetAllMessagesUseCase) {
        self.messagesUseCase = messagesUseCase
        observeMessages()
    }
    
    func observeMessages() {
        if observing { return }
        observing = true
        createPublisher(for: messagesUseCase.get())
            .receive(on: DispatchQueue.main)
            .sink { completion in
                print("Completed with \(completion)")
            } receiveValue: { [weak self] items in
                self?.messages = items
            }
            .store(in: &subscriptions)

    }
}
