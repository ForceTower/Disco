//
//  HomeDisciplinesViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 17/03/24.
//

import Combine
import Club
import KMPNativeCoroutinesCombine

class HomeDisciplinesViewModel : ObservableObject {
    private var subscriptions = Set<AnyCancellable>()
    private let disciplinesUseCase: GetDisciplinesUseCase
    
    @Published var semesters: [SemesterClassData] = []
    
    init(disciplinesUseCase: GetDisciplinesUseCase) {
        self.disciplinesUseCase = disciplinesUseCase
        observeDisciplines()
    }
    
    func observeDisciplines() {
        createPublisher(for: disciplinesUseCase.classData())
            .receive(on: DispatchQueue.main)
            .sink { completion in
                print("Completed with \(completion)")
            } receiveValue: { [weak self] semesters in
                self?.semesters = semesters
            }
            .store(in: &subscriptions)
    }
    
    func fetchDataFor(semester: Semester) {
        createPublisher(for: disciplinesUseCase.fetchSemesterData(semester: semester.id))
            .receive(on: DispatchQueue.main)
            .sink { completion in
                switch completion {
                case .failure(let error):
                    print("Failed to download data. \(error.localizedDescription)")
                case .finished:
                    print("Data downloaded.")
                }
            } receiveValue: { _ in
                
            }
            .store(in: &subscriptions)
    }
}
