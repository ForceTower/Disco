//
//  FlowWrapper.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 16/03/24.
//

import Combine
import Club

class FlowPublisher<T: AnyObject>: Publisher {
    typealias Output = T
    typealias Failure = Error
    
    private let wrappedFlow: CommonFlow<T>
    private var job: Kotlinx_coroutines_coreJob? = nil
    
    init(wrappedFlow: CommonFlow<T>) {
        self.wrappedFlow = wrappedFlow
    }
    
    func receive<S>(subscriber: S) where S : Subscriber, Error == S.Failure, T == S.Input {
        job = wrappedFlow.subscribe3(onEach: { value in
            let _ = subscriber.receive(value!)
        }, onComplete: {
            subscriber.receive(completion: .finished)
        }, onThrow: { error in
            subscriber.receive(completion: .failure(error))
        })
    }
    
    func cancel() {
        job?.cancel(cause: nil)
    }
}
