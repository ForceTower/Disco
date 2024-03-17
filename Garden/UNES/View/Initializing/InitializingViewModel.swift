//
//  InitializingViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 16/03/24.
//

import SwiftUI
import KMPNativeCoroutinesAsync
import Club

class InitializingViewModel : ObservableObject {
    private let connectedUser: ConnectedUserUseCase
    
    var router: RootRouter? = nil
    
    init(connectedUser: ConnectedUserUseCase) {
        self.connectedUser = connectedUser
    }
    
    func load() {
        Task {
            await doLoad()
        }
    }
    
    private func doLoad() async {
        var connected = false
        do {
            connected = try await asyncFunction(for: connectedUser.hasAccess()).boolValue
        } catch {
            print("Failed to retrieve state \(error.localizedDescription)")
        }
        
        DispatchQueue.main.async { [weak self, connected] in
            if connected {
                self?.router?.state = .connected
            } else {
                self?.router?.state = .login
            }
        }
    }
}
