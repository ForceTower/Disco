//
//  HomeMenuViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 18/03/24.
//

import Combine
import Club
import FirebaseCrashlytics
import KMPNativeCoroutinesCombine
import KMPNativeCoroutinesAsync

enum MenuDestination {
    case account, zhonyas, restaurant, finalCountdown, about, settings, syncRegistry, logout
}

struct MenuSection: Hashable {
    let title: String?
    let items: [MenuItem]
}

struct MenuItem: Hashable {
    let name: String
    let icon: String?
    let destination: MenuDestination
    let navigates: Bool
}

class HomeMenuViewModel : ObservableObject {
    static let services: [MenuItem] = [
        .init(name: "Conta UNES", icon: "person.crop.circle", destination: .account, navigates: true),
        .init(name: "Paradoxo de Zhonyas", icon: "timelapse", destination: .zhonyas, navigates: true),
        .init(name: "Bandejão", icon: "cup.and.saucer", destination: .restaurant, navigates: true),
        .init(name: "Final Countdown", icon: "list.dash.header.rectangle", destination: .finalCountdown, navigates: true),
    ]
    
    static let definitions: [MenuItem] = [
        .init(name: "Configurações", icon: "gear", destination: .settings, navigates: true),
        .init(name: "Sobre o aplicativo", icon: "info.circle", destination: .about, navigates: true),
        .init(name: "Registro de Sincronização", icon: "arrow.triangle.2.circlepath", destination: .syncRegistry, navigates: true),
    ]
    
    static let other: [MenuItem] = [
        .init(name: "Sair", icon: nil, destination: .logout, navigates: false)
    ]
    
    static let sections: [MenuSection] = [
        .init(title: "Serviços", items: services),
        .init(title: "Definições", items: definitions),
        .init(title: nil, items: other),
    ]
    
    private let user: ConnectedUserUseCase
    private let account: GetAccountUseCase
    
    private var subscriptions = Set<AnyCancellable>()
    @Published private(set) var currentProfile: Profile? = nil
    @Published private(set) var currentAccount: ServiceAccount? = nil
    @Published private(set) var semestersCount: Int? = nil
    
    var router: RootRouter? = nil
    
    init(user: ConnectedUserUseCase = AppDIContainer.shared.resolve(),
         account: GetAccountUseCase = AppDIContainer.shared.resolve()) {
        self.user = user
        self.account = account
        fetchProfile()
        fetchSemestersCount()
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
    
    func logout() async {
        do {
            let _ = try await asyncFunction(for: user.logout())
            UserDefaults.standard.removeObject(forKey: "old_values_sync_data")
        } catch {
            print("Failed to logout: \(error.localizedDescription)")
            Crashlytics.crashlytics().record(error: error)
        }
        
        DispatchQueue.main.async { [weak self] in
            self?.router?.state = .login
        }
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
