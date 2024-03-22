//
//  MessagingUseCase.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 21/03/24.
//

import Foundation
import KMPNativeCoroutinesAsync
import Club

class MessagingUseCase {
    private let messaging: MessagingTokenUseCase
    
    init(messaging: MessagingTokenUseCase) {
        self.messaging = messaging
    }
    
    func onTokenReceived(_ token: String) {
        Task { await doSendToken(token) }
    }
    
    private func doSendToken(_ token: String) async {
        do {
            let _ = try await asyncFunction(for: messaging.sendFcmTokenIfNeeded(token: token))
            print("Token sent")
        } catch {
            print("Failed to send token \(error.localizedDescription)")
        }
    }
}
