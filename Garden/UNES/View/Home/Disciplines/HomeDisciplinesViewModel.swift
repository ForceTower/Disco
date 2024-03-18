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
    private let syncUseCase: LocalSyncDataUseCase
    
    @Published var semesters: [SemesterClassData] = []
    @Published private(set) var loading = false
    
    init(disciplinesUseCase: GetDisciplinesUseCase, syncUseCase: LocalSyncDataUseCase) {
        self.disciplinesUseCase = disciplinesUseCase
        self.syncUseCase = syncUseCase
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
    
    func syncData() async {
        if loading { return }
        DispatchQueue.main.async { [weak self] in
            self?.loading = true
        }
        
        let result = await syncUseCase.execute(loadDetails: false)
        print("Sync result: \(result)")
        
        DispatchQueue.main.async { [weak self] in
            self?.loading = false
        }
    }
}
