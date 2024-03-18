//
//  ScheduleBackgroundProcessingUseCase.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 18/03/24.
//

import BackgroundTasks
import FirebaseCrashlytics

class ScheduleBackgroundProcessingUseCase {
    func scheduleAppRefresh() {
        let request = BGProcessingTaskRequest(identifier: "dev.forcetower.unes.ios.apprefresh")
        request.earliestBeginDate = Date(timeIntervalSinceNow: 15 * 60)
        do {
            try BGTaskScheduler.shared.submit(request)
            print("Scheduled task")
        } catch(let error) {
            print("Scheduling error \(error)")
            Crashlytics.crashlytics().log("Failed to schedule app refresh")
            Crashlytics.crashlytics().record(error: error)
        }
    }
}
