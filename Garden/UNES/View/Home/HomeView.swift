//
//  HomeView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 16/03/24.
//

import SwiftUI

struct HomeView: View {
    @StateObject private var viewModel: HomeViewModel = .init()
    
    var body: some View {
        TabView(selection: $viewModel.tabSelection,
                content:  {
            HomeDashboardView(selectSchedule: {
                viewModel.tabSelection = .schedule
            }, selectMessages: {
                viewModel.tabSelection = .messages
            })
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
            
            HomeMenuView()
                .tabItem {
                    Label("Menu", systemImage: "circle.grid.2x2")
                }.tag(HomeTabSelection.others)
        })
        .onAppear {
            NotificationManager.shared.requestPermission()
        }
    }
}

#Preview {
    HomeView()
}
