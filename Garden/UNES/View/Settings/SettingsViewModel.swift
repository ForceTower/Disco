//
//  SettingsViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 19/03/24.
//

import Combine

class SettingsViewModel : ObservableObject {
    private let scheduleSync: ScheduleBackgroundProcessingUseCase
    
    init(scheduleSync: ScheduleBackgroundProcessingUseCase = AppDIContainer.shared.resolve()) {
        self.scheduleSync = scheduleSync
    }
    
    func changeSetting(syncFrequency: FrequencyOption) {
        scheduleSync.cancelAppRefresh()
        scheduleSync.scheduleAppRefresh(frequency: syncFrequency)
    }
}
