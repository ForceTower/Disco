//
//  BigTrayView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 18/03/24.
//

import SwiftUI

struct BigTrayView: View {
    @Binding var path: NavigationPath
    @StateObject private var vm: BigTrayViewModel = .init()
    
    var body: some View {
        VStack {
            if let data = vm.data {
                if data.isOpen() {
                    VStack {
                        Spacer()
                        VStack {
                            Text("Ainda restam")
                            
                            Text(data.quota)
                                .font(.largeTitle)
                                .fontWeight(.medium)
                                .foregroundStyle(.blue)
                            
                            Text("refeições")
                                .padding(.bottom)
                            
                            ProgressView(value: Float(data.quotaInt()), total: Float(data.maxQuota()))
                            
                            if let meal = currentMeal() {
                                Text("Hora do \(meal)!")
                                Text(data.getNextMealTime())
                                Text("Eu acho...")
                                    .font(.footnote)
                            }
                        }
                        .padding(.bottom, 56)
                        Spacer()
                        Text("Última atualização\n\(Date(timeIntervalSince1970: TimeInterval(data.time / 1000)).formatted(date: .abbreviated, time: .standard))")
                            .multilineTextAlignment(.center)
                            .font(.footnote)
                    }
                    .padding(.horizontal)
                } else {
                    Text("O Restaurante está fechado")
                }
            } else {
                ProgressView()
            }
        }
        .navigationTitle("Bandejão")
    }
    
    func currentMeal() -> String? {
        guard let data = vm.data else { return nil }
        let type = data.getNextMealType()
        
        if type == 1 { return "Café da manhã" }
        if type == 2 { return "Almoço" }
        return "Jantar"
    }
}

#Preview {
    @State var path: NavigationPath = .init()
    return NavigationStack(path: $path) {
        BigTrayView(path: $path)
    }
}
