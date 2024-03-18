//
//  DisciplineDetailsViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 17/03/24.
//

import Club
import Combine
import KMPNativeCoroutinesCombine

class DisciplineDetailsViewModel : ObservableObject {
    private let classData: ClassDataUseCase
    
    private var subscriptions = Set<AnyCancellable>()
    private var loaded: Int64? = nil
    
    @Published var data: ClassGroupData? = nil
    @Published var absences: [ClassAbsence] = []
    @Published var materials: [ClassMaterial] = []
    @Published var items: [ClassItem] = []
    
    init(classData: ClassDataUseCase) {
        self.classData = classData
    }
    
    func loadDataFor(groupId: Int64) {
        if loaded == groupId { return }
        loaded = groupId
        
        createPublisher(for: classData.groupDetails(groupId: groupId))
            .receive(on: DispatchQueue.main)
            .sink { _ in
                print("Completed details")
            } receiveValue: { [weak self] data in
                self?.data = data
            }
            .store(in: &subscriptions)
        
        createPublisher(for: classData.materials(groupId: groupId))
            .receive(on: DispatchQueue.main)
            .sink { _ in
                
            } receiveValue: { [weak self] data in
                self?.materials = data
            }
            .store(in: &subscriptions)
        
        createPublisher(for: classData.absences(groupId: groupId))
            .receive(on: DispatchQueue.main)
            .sink { _ in
                
            } receiveValue: { [weak self] data in
                self?.absences = data
            }
            .store(in: &subscriptions)
        
        createPublisher(for: classData.groupItems(groupId: groupId))
            .receive(on: DispatchQueue.main)
            .sink { _ in

            } receiveValue: { [weak self] data in
                self?.items = data
            }
            .store(in: &subscriptions)
    }
    
    func fetchDataFor(groupId: Int64) {
        createPublisher(for: classData.fetchDataFor(groupId: groupId))
            .receive(on: DispatchQueue.main)
            .sink { completion in
                switch completion {
                case .finished:
                    print("Update data success")
                case .failure(let err):
                    print("Failed to update with error. \(err.localizedDescription)")
                }
            } receiveValue: { _ in
                
            }
            .store(in: &subscriptions)
    }
}
