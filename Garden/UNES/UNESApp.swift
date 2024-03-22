//
//  UNESApp.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 25/02/24.
//

import SwiftUI
import Club
import FirebaseCrashlytics

@main
struct UNESApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    @ObservedObject var router = RootRouter()
    @Environment(\.scenePhase) var scenePhase
    @AppStorage("settings_sync_frequency") private var selectedFrequency: FrequencyOption = .minutes15
    @AppStorage("settings_device_local_id") private var deviceId: String = ""
    
    init() {
        HelperKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            RootView()
                .environmentObject(router)
                .onChange(of: scenePhase) { _ in
//                    if next == .background {
//                        let scheduler: ScheduleBackgroundProcessingUseCase = AppDIContainer.shared.resolve()
//                        scheduler.scheduleAppRefresh(frequency: selectedFrequency)
//                    }
                }
                .onAppear {
                    if deviceId.isEmpty {
                        let uuid = UUID().uuidString
                        let idx = uuid.firstIndex(of: "-") ?? uuid.endIndex
                        deviceId = String(uuid[uuid.startIndex..<idx])
                    }
                    Crashlytics.crashlytics().setUserID(deviceId)
                }
        }
    }
}
