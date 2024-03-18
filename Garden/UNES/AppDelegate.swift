//
//  AppDelegate.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 18/03/24.
//

import Foundation
import UIKit
import FirebaseCore
import FirebaseAnalytics
import FirebaseCrashlytics
import FirebaseMessaging
import BackgroundTasks

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        FirebaseApp.configure()
#if DEBUG
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(false)
#endif
        
        BGTaskScheduler.shared.register(forTaskWithIdentifier: "dev.forcetower.unes.ios.apprefresh", using: nil) { task in
            switch task {
            case let task as BGProcessingTask:
                self.handleAppRefresh(task)
            default:
                print("Unknown task type :)")
            }
        }
        
        UNUserNotificationCenter.current().delegate = self
        Messaging.messaging().delegate = self
        application.registerForRemoteNotifications()
        return true
    }
    
    private func handleAppRefresh(_ task: BGProcessingTask) {
        Analytics.logEvent("app_background_fetch", parameters: nil)
        var fetchTask: Task<Void, Never>? = nil
        
        let scheduler: ScheduleBackgroundProcessingUseCase = AppDIContainer.shared.resolve()
        scheduler.scheduleAppRefresh()
        
        fetchTask = Task {
            UserDefaults.standard.set(Date(), forKey: "last_sync")
            let sync: LocalSyncDataUseCase = AppDIContainer.shared.resolve()
            let result = await sync.execute()
            Analytics.logEvent("app_background_fetch_complete", parameters: nil)
            task.setTaskCompleted(success: result)
        }
        
        task.expirationHandler = {
            Analytics.logEvent("app_background_fetch_canceled", parameters: nil)
            fetchTask?.cancel()
        }
    }
}

extension AppDelegate: UNUserNotificationCenterDelegate, MessagingDelegate {
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }
    
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        if let fcmToken = fcmToken {
            print("Received fcm token: \(fcmToken)")
        }
    }
}
