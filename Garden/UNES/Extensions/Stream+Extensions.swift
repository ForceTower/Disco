//
//  Stream+Extensions.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 10/03/24.
//

import Combine

extension PassthroughSubject where Failure == Error {
    static func emittingValues<T: AsyncSequence>(
        from sequence: T
    ) -> Self where T.Element == Output {
        let subject = Self()
        
        Task {
            do {
                for try await value in sequence {
                    subject.send(value)
                }
                
                subject.send(completion: .finished)
            } catch {
                subject.send(completion: .failure(error))
            }
        }
        
        return subject
    }
}
