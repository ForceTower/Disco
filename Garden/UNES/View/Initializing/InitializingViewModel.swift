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
        print("Loading..")
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
        
        print("Got result! \(connected)")
        
        DispatchQueue.main.async { [weak self, connected] in
            print("Has self? \(self != nil) and router? \(self?.router != nil)")
            if connected {
                self?.router?.state = .connected
            } else {
                self?.router?.state = .login
            }
        }
    }
}
