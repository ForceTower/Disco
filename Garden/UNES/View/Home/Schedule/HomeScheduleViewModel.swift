//
//  HomeScheduleViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 17/03/24.
//

import Club
import Combine
import KMPNativeCoroutinesCombine

class HomeScheduleViewModel : ObservableObject {
    private let scheduleUseCase: GetScheduleUseCase
    private var subscriptions = Set<AnyCancellable>()
    
    @Published var blocks: [BlockLine] = []
    @Published var lines: [LinedClassLocation] = []
    
    init(scheduleUseCase: GetScheduleUseCase) {
        self.scheduleUseCase = scheduleUseCase
        fetchSchedule()
    }
    
    func fetchSchedule() {
        createPublisher(for: scheduleUseCase.currentSchedule(showEmptyDays: false))
            .receive(on: DispatchQueue.main)
            .sink { completion in
                print("Finished.")
            } receiveValue: { [weak self] data in
                self?.hostSchedule(data)
            }
            .store(in: &subscriptions)

    }
    
    func hostSchedule(_ data: ScheduleData) {
        blocks = data.block.schedule
        lines = data.line.line
    }
}
