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
    private let account: GetAccountUseCase
    private let user: ConnectedUserUseCase
    
    private var subscriptions = Set<AnyCancellable>()
    
    @Published private(set) var currentClass: ExtendedClassLocationData? = nil
    @Published private(set) var latestMessage: Message? = nil
    @Published private(set) var currentProfile: Profile? = nil
    @Published private(set) var currentAccount: ServiceAccount? = nil
    @Published private(set) var semestersCount: Int? = nil
    
    init(
        schedule: GetScheduleUseCase,
        messages: GetAllMessagesUseCase,
        user: ConnectedUserUseCase,
        account: GetAccountUseCase = AppDIContainer.shared.resolve()
    ) {
        self.schedule = schedule
        self.messages = messages
        self.user = user
        self.account = account
        
        fetchCurrentClass()
        fetchMessage()
        fetchProfile()
        fetchSemestersCount()
        fetchAccount()
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
    
    private func fetchAccount() {
        createPublisher(for: account.getAccount())
            .receive(on: DispatchQueue.main)
            .sink { completion in
                print("Received flow account completion \(completion)")
            } receiveValue: { [weak self] account in
                self?.currentAccount = account
            }
            .store(in: &subscriptions)
    }
    
    private func fetchSemestersCount() {
        createPublisher(for: user.semestersCount())
            .receive(on: DispatchQueue.main)
            .sink { _ in
                
            } receiveValue: { [weak self] count in
                self?.semestersCount = count.intValue
            }
            .store(in: &subscriptions)
    }
    
    func findUserSubtitle(opt option: SubtitleOption) -> String? {
        if option == .none { return nil }
        
        let university = "Universidade Estadual de Feira de Santana"
        if option == .university { return university }
        
        let course = currentProfile?.platformCourseValue
        if option == .course { return course ?? university }
        
        var semesterText: String?
        if let semesters = semestersCount {
            semesterText = "Você está no \(semesters)º semestre"
        }
        
        if option == .semester {
            return semesterText ?? course ?? university
        }
        
        var scoreText: String?
        
        if let score = currentProfile?.calcScore {
            scoreText = String(format: "Seu score calculado é %.1f", score)
        }
        
        return scoreText ?? semesterText ?? course ?? university
    }
}
