//
//  HomeViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 16/03/24.
//

import SwiftUI
import Club
import FirebaseCrashlytics
import KMPNativeCoroutinesAsync

enum HomeTabSelection: Hashable {
    case dashboard, schedule, messages, disciplines, others
}

class HomeViewModel : ObservableObject {
    private let disciplinesUseCase: GetDisciplinesUseCase
    private let messaging: MessagingUseCase
    
    @Published var tabSelection: HomeTabSelection = .dashboard
    private var loadedMissing = false
    private var sentMessagingToken = false
    
    init(disciplinesUseCase: GetDisciplinesUseCase = AppDIContainer.shared.resolve(),
         messaging: MessagingUseCase = AppDIContainer.shared.resolve()) {
        self.disciplinesUseCase = disciplinesUseCase
        self.messaging = messaging
    }
    
    func loadMissingSemesters() {
        if loadedMissing { return }
        loadedMissing = true
        Task { await doLoadMissing() }
    }
    
    func sendTokenIfNeeded() {
        if sentMessagingToken { return }
        sentMessagingToken = true
        guard let token = UserDefaults.standard.string(forKey: "messaging_notification_token") else { return }
        messaging.onTokenReceived(token)
    }
    
    private func doLoadMissing() async {
        do {
            let data = UserDefaults.standard.array(forKey: "old_values_sync_data") as? [Int64] ?? []
            let kotlin = data.map { KotlinLong(value: $0) }
            let result = try await asyncFunction(for: disciplinesUseCase.loadMissingSemesters(loaded: kotlin))
            let swift = result.map { $0.int64Value }
            UserDefaults.standard.set(swift, forKey: "old_values_sync_data")
        } catch {
            print("Failed to load old things: \(error.localizedDescription)")
            Crashlytics.crashlytics().record(error: error)
        }
    }
}
