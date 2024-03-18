//
//  LocalSyncDataUseCase.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 18/03/24.
//

import KMPNativeCoroutinesAsync
import KMPNativeCoroutinesCombine
import FirebaseCrashlytics
import Club

class LocalSyncDataUseCase {
    private let sync: SyncDataUseCase
    private let notifications: PendingNotificationsUseCase
    
    init(sync: SyncDataUseCase, notifications: PendingNotificationsUseCase) {
        self.sync = sync
        self.notifications = notifications
    }
    
    func execute() async -> Bool {
        let result = await doExecute()
        await checkNotifications()
        return result
    }
    
    func checkNotifications() async {
        do {
            let messages = try await asyncFunction(for: notifications.messages(markNotified: true))
            messages.forEach { message in
                NotificationManager.shared.createNotification(forMessage: message)
            }
            let grades = try await asyncFunction(for: notifications.grades(markNotified: true))
            grades.forEach { grade in
                NotificationManager.shared.createNotification(forGrade: grade)
            }
        } catch {
            print("Failed to post notifications. \(error.localizedDescription)")
            Crashlytics.crashlytics().log("Failed to post notifications: \(error.localizedDescription)")
            Crashlytics.crashlytics().record(error: error)
        }
    }
    
    private func doExecute() async -> Bool {
        do {
            let result = try await asyncFunction(for: sync.execute())
            switch result {
            case is SyncResult.Completed:
                return true
            case is SyncResult.NoOp:
                return true
            case is SyncResult.InvalidSemester:
                return true
            case is SyncResult.InvalidCredentials:
                // Display notification
                return true
            case let error as SyncResult.LoginError:
                print(error.error)
                Crashlytics.crashlytics().log("Sync failed: Login Error. \(error.error)")
                Crashlytics.crashlytics().record(error: NSError(domain: "Sync Failed: Login Error", code: 1110))
                return false
            case let other as SyncResult.OtherError:
                print(other.error)
                Crashlytics.crashlytics().log("Sync Failed: Other Error. \(other.error)")
                Crashlytics.crashlytics().record(error: NSError(domain: "Sync Failed: Other Error", code: 1115))
                return false
            default:
                print("Unknown result")
                // Unknown result.
                Crashlytics.crashlytics().log("Sync Failed: Unknown reason. \(String(describing: result))")
                Crashlytics.crashlytics().record(error: NSError(domain: "Sync Failed: Unknown reason", code: 1120))
                return false
            }
        } catch {
            print("Catch \(error.localizedDescription)")
            Crashlytics.crashlytics().log("Sync Failed: \(error.localizedDescription)")
            Crashlytics.crashlytics().record(error: error)
            return false
        }
    }
}
