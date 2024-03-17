//
//  UNESApp.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 25/02/24.
//

import SwiftUI
import Club

@main
struct UNESApp: App {
    init() {
        HelperKt.doInitKoin()
    }

    var body: some Scene {
        @ObservedObject var router = RootRouter()
        WindowGroup {
            RootView()
                .environmentObject(router)
        }
    }
}
