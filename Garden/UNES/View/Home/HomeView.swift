//
//  HomeView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 16/03/24.
//

import SwiftUI

struct HomeView: View {
    @StateObject private var vm: HomeViewModel = .init()
    @State private var menuPath = NavigationPath()
    
    var selectionBinding: Binding<HomeTabSelection> {
        Binding(
            get: { vm.tabSelection },
            set: {
                if $0 == vm.tabSelection && $0 == .others {
                    menuPath.removeLast(menuPath.count)
                }
                vm.tabSelection = $0
            }
        )
    }
    
    var body: some View {
        TabView(selection: selectionBinding) {
            HomeDashboardView {
                vm.tabSelection = .schedule
            } selectMessages: {
                vm.tabSelection = .messages
            }
            .tabItem {
                Label("Início", systemImage: "newspaper")
            }.tag(HomeTabSelection.dashboard)
            
            HomeScheduleView()
                .tabItem {
                    Label("Horários", systemImage: "clock")
                }
                .tag(HomeTabSelection.schedule)
            
            HomeMessagesView()
                .tabItem {
                    Label("Mensagens", systemImage: "envelope")
                }.tag(HomeTabSelection.messages)
            
            HomeDisciplinesView()
                .tabItem {
                    Label("Disciplinas", systemImage: "graduationcap")
                }.tag(HomeTabSelection.disciplines)
            
            NavigationStack(path: $menuPath) {
                HomeMenuView(path: $menuPath)
            }
            .tabItem {
                Label("Menu", systemImage: "circle.grid.2x2")
            }
            .tag(HomeTabSelection.others)
        }
        .onAppear {
            NotificationManager.shared.requestPermission()
            vm.loadMissingSemesters()
            vm.sendTokenIfNeeded()
        }
    }
}

#Preview {
    HomeView()
        .environmentObject(RootRouter())
}
