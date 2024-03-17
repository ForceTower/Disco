//
//  HomeViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 16/03/24.
//

import SwiftUI

enum HomeTabSelection: Hashable {
    case dashboard, schedule, messages, disciplines, others
}

class HomeViewModel : ObservableObject {
    @Published var tabSelection: HomeTabSelection = .dashboard
}
