//
//  RootView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 25/02/24.
//

import SwiftUI

struct RootView: View {
    @ObservedObject var router = RootRouter()
    
    var body: some View {
        if (router.currentRoot == 0) {
            AuthRootView()
        }
    }
}

#Preview {
    RootView()
}
