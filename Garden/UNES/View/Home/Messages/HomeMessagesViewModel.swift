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
    // [.init(id: 33, content: "Message", platformId: 54, timestamp: Int64(Date().timeIntervalSince1970 * 1000), senderProfile: 3, senderName: "Marina", notified: 1, discipline: "Magica", uuid: "fddfdfd", codeDiscipline: "EXA110", html: 0, dateString: "Grande dia", processingTime: 0, hashMessage: 12, attachmentName: nil, attachmentLink: nil)]
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
