//
//  RootView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 25/02/24.
//

import SwiftUI

struct RootView: View {
    @EnvironmentObject var router: RootRouter
    
    var body: some View {
        switch router.state {
        case .login:
            AuthRootView()
        case .connected:
            HomeView()
        case .initializing:
            InitializingView()
        }
    }
}

#Preview {
    RootView()
        .environmentObject(RootRouter())
}
