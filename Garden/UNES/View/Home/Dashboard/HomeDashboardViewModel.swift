//
//  DashboardViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 16/03/24.
//

import Club
import Combine
import KMPNativeCoroutinesCombine

class HomeDashboardViewModel : ObservableObject {
    private let schedule: GetScheduleUseCase
    private let messages: GetAllMessagesUseCase
    private let user: ConnectedUserUseCase
    
    private var subscriptions = Set<AnyCancellable>()
    
    @Published private(set) var currentClass: ExtendedClassLocationData? = nil
    @Published private(set) var latestMessage: Message? = nil
    @Published private(set) var currentProfile: Profile? = nil
    
    init(schedule: GetScheduleUseCase, messages: GetAllMessagesUseCase, user: ConnectedUserUseCase) {
        self.schedule = schedule
        self.messages = messages
        self.user = user
        
        fetchCurrentClass()
        fetchMessage()
        fetchProfile()
    }
    
    func fetchCurrentClass() {
        createPublisher(for: schedule.currentClass(delayMs: 10000))
            .receive(on: DispatchQueue.main)
            .sink { _ in
                
            } receiveValue: { [weak self] clazz in
                self?.currentClass = clazz
            }
            .store(in: &subscriptions)
    }
    
    func fetchMessage() {
        createPublisher(for: messages.last())
            .receive(on: DispatchQueue.main)
            .sink { _ in
                
            } receiveValue: { [weak self] message in
                self?.latestMessage = message
            }
            .store(in: &subscriptions)
    }
    
    func fetchProfile() {
        createPublisher(for: user.currentProfile())
            .receive(on: DispatchQueue.main)
            .sink { _ in
                
            } receiveValue: { [weak self] profile in
                self?.currentProfile = profile
            }
            .store(in: &subscriptions)
    }
}
