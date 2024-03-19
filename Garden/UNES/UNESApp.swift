//
//  UNESApp.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 25/02/24.
//

import SwiftUI
import Club

@main
struct UNESApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    @ObservedObject var router = RootRouter()
    @Environment(\.scenePhase) var scenePhase
    @AppStorage("settings_sync_frequency") private var selectedFrequency: FrequencyOption = .minutes15
    
    init() {
        HelperKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            RootView()
                .environmentObject(router)
                .onChange(of: scenePhase) { next in
                    if next == .background {
                        let scheduler: ScheduleBackgroundProcessingUseCase = AppDIContainer.shared.resolve()
                        scheduler.scheduleAppRefresh(frequency: selectedFrequency)
                    }
                }
        }
    }
}
