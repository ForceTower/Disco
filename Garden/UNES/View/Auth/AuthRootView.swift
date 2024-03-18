//
//  AuthRootView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 25/02/24.
//

import SwiftUI

struct AuthRootView: View {
    @State var path: NavigationPath = .init()
    
    var body: some View {
        NavigationStack(path: $path) {
            AuthWelcomeView(path: $path.animation(.easeOut))
                .navigationDestination(for: String.self) { tag in
                    if tag == "PortalAuth" {
                        AuthPortalLoginView(path: $path)
                    } else if tag == "UnesAuth" {
                        AuthUnesLoginView(path: $path)
                    }
                }
        }
        .onAppear {
            NotificationManager.shared.requestPermission()
        }
    }
}

#Preview {
    AuthRootView()
}
