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
                    .padding()
                } else if !data.error {
                    VStack {
                        Spacer()
                        Image(uiImage: UIImage(resource: .fineMeal))
                            .resizable()
                            .scaledToFit()
                            .frame(height: 200)
                        Text("O restaurante está fechado")
                        Spacer()
                        Text("Última atualização\n\(Date(timeIntervalSince1970: TimeInterval(data.time / 1000)).formatted(date: .abbreviated, time: .standard))")
                            .multilineTextAlignment(.center)
                            .font(.footnote)
                    }
                } else {
                    VStack {
                        Spacer()
                        Image(systemName: "xmark.seal.fill")
                            .resizable()
                            .scaledToFit()
                            .foregroundStyle(.red)
                            .frame(height: 100)
                        Text("Erro ao carregar dados\nTentarei recarregar em breve")
                            .multilineTextAlignment(.center)
                            .padding(.top)
                        Spacer()
                        Text("Última atualização\n\(Date(timeIntervalSince1970: TimeInterval(data.time / 1000)).formatted(date: .abbreviated, time: .standard))")
                            .multilineTextAlignment(.center)
                            .font(.footnote)
                    }
                }
            } else {
                ProgressView("Carregando dados...")
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
