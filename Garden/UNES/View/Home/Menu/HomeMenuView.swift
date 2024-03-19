//
//  HomeMenuView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 18/03/24.
//

import SwiftUI
import SDWebImageSwiftUI

struct HomeMenuView: View {
    @State var path: NavigationPath = .init()
    @StateObject private var vm: HomeMenuViewModel = .init()
    @State var showLogoutSheet = false
    
    var body: some View {
        NavigationStack(path: $path) {
            List {
                HStack {
                    VStack(alignment: .leading) {
                        if let name = vm.currentProfile?.name {
                            Text(name)
                                .font(.title3)
                            Text("Engenharia de Computação")
                                .font(.footnote)
                                .fontWeight(.regular)
                        }
                    }
                    Spacer()
                    if let imageUrl = vm.currentProfile?.imageUrl {
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
        }
    }
    
    func onLogoutConfirmed() {
        showLogoutSheet = false
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
    HomeMenuView()
}
