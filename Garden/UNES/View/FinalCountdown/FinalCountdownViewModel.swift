//
//  FinalCountdownViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 18/03/24.
//

import Combine
import Foundation

struct MechValue: Hashable, Identifiable {
    let id = UUID().uuidString
    let weight: Double
    let grade: Double?
}

struct MechResult {
    let mean: Double
    let wildcard: Double?
    let finalGrade: Double?
    let final: Bool
    let lost: Bool
}

class FinalCountdownViewModel : ObservableObject {
    @Published var values: [MechValue] = []
    @Published private(set) var result: MechResult? = nil
    
    func addValue(_ value: MechValue) {
        values.append(value)
        calc()
    }
    
    func deleteValue(_ set: IndexSet) {
        values.remove(atOffsets: set)
        if values.isEmpty {
            result = nil
        } else {
            calc()
        }
    }
    
    func updateItem(_ old: MechValue, _ next: MechValue) {
        let idx = values.firstIndex(of: old) ?? 0
        values.remove(at: idx)
        values.insert(next, at: idx)
        calc()
    }
    
    private func calc() {
        let desiredMean = 7.0
        
        var wildcardWeight = 0.0
        var gradesSum = 0.0
        var weightSum = 0.0
        
        for value in values {
            let grade = value.grade
            
            if let grade = grade {
                gradesSum += truncateDouble(grade * value.weight)
                weightSum += value.weight
            } else {
                wildcardWeight += value.weight
            }
        }
        
        let rightEquation = truncateDouble((weightSum + wildcardWeight) * desiredMean)
        
        if wildcardWeight == 0.0 {
            let mean = truncateDouble(gradesSum / weightSum)
            if (gradesSum >= rightEquation) {
                result = .init(mean: mean, wildcard: nil, finalGrade: nil, final: false, lost: false)
            } else {
                let mech = onFinals(mean, needsWildcard: false)
                result = mech
            }
        } else {
            let newRight = rightEquation - gradesSum
            let wildcard = truncateDouble(newRight / wildcardWeight)
            let normWildcard = min(wildcard, 10.0)
            
            let additional = values.reduce(0.0) { partialResult, item in
                if item.grade == nil {
                    return truncateDouble(normWildcard * item.weight)
                }
                return partialResult
            }
            
            let finalGrade = gradesSum + additional
            let finalWeight = weightSum + wildcardWeight
            let theMean = truncateDouble(finalGrade / finalWeight)
            if wildcard > 10 {
                let mech = onFinals(theMean, needsWildcard: true)
                result = mech
            } else {
                result = .init(mean: theMean, wildcard: wildcard, finalGrade: nil, final: false, lost: false)
            }
        }
    }
    
    private func onFinals(_ mean: Double, needsWildcard: Bool) -> MechResult {
        let wildcard: Double? = if needsWildcard { 10.0 } else { nil }
        if mean < 3 {
            return .init(mean: mean, wildcard: nil, finalGrade: nil, final: false, lost: true)
        }
        
        let finalGrade = roundDouble(12.5 - (1.5 * mean))
        if finalGrade <= 8 {
            return .init(mean: mean, wildcard: wildcard, finalGrade: finalGrade, final: true, lost: false)
        } else {
            return .init(mean: mean, wildcard: wildcard, finalGrade: finalGrade, final: true, lost: true)
        }
    }
    
    private func truncateDouble(_ value: Double, decimals: Int = 1) -> Double {
        let power = pow(10, Double(decimals))
        return floor(value * power) / power
    }
    
    private func roundDouble(_ value: Double, decimals: Int = 1) -> Double {
        let power = pow(10, Double(decimals))
        return (value * power).rounded() / power
    }
}
