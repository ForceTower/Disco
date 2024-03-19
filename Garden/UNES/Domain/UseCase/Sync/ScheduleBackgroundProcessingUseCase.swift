//
//  ScheduleBackgroundProcessingUseCase.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 18/03/24.
//

import BackgroundTasks
import FirebaseCrashlytics

class ScheduleBackgroundProcessingUseCase {
    func scheduleAppRefresh(frequency: FrequencyOption) {
        let nextMin = switch frequency {
        case .minutes15: 15
        case .minutes30: 30
        case .hour1: 60
        case .hour2: 120
        case .hour4: 240
        case .disabled: 0
        }
        
        if nextMin == 0 {
            return
        }
        
        let request = BGProcessingTaskRequest(identifier: "dev.forcetower.unes.ios.apprefresh")
        request.earliestBeginDate = Date(timeIntervalSinceNow: Double(nextMin) * 60)
        do {
            try BGTaskScheduler.shared.submit(request)
            print("Scheduled task")
        } catch(let error) {
            print("Scheduling error \(error)")
            Crashlytics.crashlytics().log("Failed to schedule app refresh")
            Crashlytics.crashlytics().record(error: error)
        }
    }
    
    func cancelAppRefresh() {
        BGTaskScheduler.shared.cancel(taskRequestWithIdentifier: "dev.forcetower.unes.ios.apprefresh")
    }
}
