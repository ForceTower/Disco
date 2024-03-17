//
//  InitializingView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 16/03/24.
//

import SwiftUI

struct InitializingView: View {
    @EnvironmentObject var router: RootRouter
    
    @StateObject var viewModel: InitializingViewModel = .init(connectedUser: AppDIContainer.shared.resolve())
    
    var body: some View {
        ZStack {
            Rectangle()
                .fill(LinearGradient(colors: [.blue, .green, .orange], startPoint: .topLeading, endPoint: .bottomTrailing))
                .opacity(0.4)
                .blur(radius: 3.0)
                .ignoresSafeArea()
            
            VStack {
                Image(.coloredLogo)
                    .resizable()
                    .scaledToFit()
                    .frame(height: 240)
                ProgressView {
                    Text("Carregando configurações")
                        .font(.subheadline)
                        .fontWeight(.light)
                }
            }
        }.onAppear(perform: {
            viewModel.router = router
            viewModel.load()
        })
    }
}

#Preview {
    InitializingView()
        .environmentObject(RootRouter())
}
