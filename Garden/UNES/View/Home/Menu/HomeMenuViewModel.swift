//
//  HomeMenuViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 18/03/24.
//

import Combine
import Club
import KMPNativeCoroutinesCombine

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
    
    private var subscriptions = Set<AnyCancellable>()
    @Published private(set) var currentProfile: Profile? = nil
    
    init(user: ConnectedUserUseCase = AppDIContainer.shared.resolve()) {
        self.user = user
        fetchProfile()
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