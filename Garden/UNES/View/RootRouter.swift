//
//  RootRouter.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 25/02/24.
//

import Foundation

enum RootState : Hashable {
    case initializing, login, connected
}

class RootRouter : ObservableObject {
    @Published var state: RootState = .initializing
}
