//
//  LocalSyncDataUseCase.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 18/03/24.
//

import KMPNativeCoroutinesAsync
import FirebaseCrashlytics
import Club

class LocalSyncDataUseCase {
    private let sync: SyncDataUseCase
    
    init(sync: SyncDataUseCase) {
        self.sync = sync
    }
    
    func execute() async -> Bool {
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
