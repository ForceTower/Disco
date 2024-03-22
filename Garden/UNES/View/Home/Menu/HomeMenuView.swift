//
//  HomeMenuView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 18/03/24.
//

import SwiftUI
import SDWebImageSwiftUI

struct HomeMenuView: View {
    @Binding var path: NavigationPath
    @EnvironmentObject var router: RootRouter
    @AppStorage("settings_exhibition_subtitle") private var subtitleOption: SubtitleOption = .score
    
    @StateObject private var vm: HomeMenuViewModel = .init()
    @State var showLogoutSheet = false
    
    var body: some View {
        List {
            NavigationLink(value: MenuItem(name: "", icon: "", destination: .account, navigates: false)) {
                HStack {
                    if let imageUrl = vm.currentAccount?.imageUrl {
                        WebImage(url: URL(string: imageUrl)) { image in
                            image.resizable()
                        } placeholder: {
                            Rectangle().foregroundColor(.gray)
                        }
                        .indicator(.activity)
                        .scaledToFit()
                        .transition(.fade(duration: 0.5))
                        .frame(width: 48, height: 48)
                        .clipShape(.circle)
                        .padding(.trailing, 8)
                    }
                    VStack(alignment: .leading) {
                        if let profile = vm.currentProfile, let name = profile.name {
                            Text(name)
                                .font(.title3)
                            
                            if let subtitle = vm.findUserSubtitle(opt: subtitleOption) {
                                Text(subtitle)
                                    .font(.footnote)
                                    .fontWeight(.regular)
                            }
                        }
                    }
                }
            }
            
            ForEach(HomeMenuViewModel.sections, id: \.title) { section in
                if let title = section.title {
                    Section(title) {
                        SectionItemView(section: section) { item in
                            onItemTapped(item)
                        }
                    }
                } else {
                    Section {
                        SectionItemView(section: section) { item in
                            onItemTapped(item)
                        }
                    }
                }
            }
        }
        .navigationDestination(for: MenuItem.self) { item in
            if item.destination == .restaurant {
                BigTrayView(path: $path)
            } else if item.destination == .finalCountdown {
                FinalCountdownView()
            } else if item.destination == .syncRegistry {
                SyncRegistryView()
            } else if item.destination == .settings {
                SettingsView()
            } else if item.destination == .about {
                AboutView()
            } else if item.destination == .zhonyas {
                ParadoxView()
            } else if item.destination == .account {
                AccountView(path: $path)
            }
        }
        .confirmationDialog("Sair do UNES", isPresented: $showLogoutSheet, titleVisibility: .visible) {
            Button(role: .destructive, action: {
                onLogoutConfirmed()
            }, label: {
                Text("Sair e apagar dados")
            })
        } message: {
            Text("Tem certeza que deseja sair? Todos os dados locais serão apagados.")
        }
        .navigationTitle("Menu")
        .onAppear {
            vm.router = router
        }
    }

    func onLogoutConfirmed() {
        showLogoutSheet = false
        Task { await vm.logout() }
    }
    
    func onItemTapped(_ item: MenuItem) {
        if item.destination == .logout {
            showLogoutSheet = true
        }
    }
}

struct SectionItemView: View {
    let section: MenuSection
    let onItemTapped: (MenuItem) -> Void
    
    var body: some View {
        ForEach(section.items, id: \.destination) { item in
            if item.navigates {
                NavigationLink(value: item) {
                    if let icon = item.icon {
                        Label(
                            title: { Text(item.name) },
                            icon: { Image(systemName: icon) }
                        )
                    } else {
                        Text(item.name)
                    }
                }
            } else {
                Button {
                    onItemTapped(item)
                } label: {
                    Text(item.name)
                }
            }
        }
    }
}

#Preview {
    @State var path = NavigationPath()
    return NavigationStack(path: $path) {
        HomeMenuView(path: $path)
            .environmentObject(RootRouter())
    }
}
