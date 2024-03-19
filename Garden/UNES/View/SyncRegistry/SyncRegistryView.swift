//
//  SyncRegistryView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 19/03/24.
//

import Club
import SwiftUI

struct SyncRegistryView: View {
    @StateObject private var vm: SyncRegistryViewModel = .init()
    
    var body: some View {
        List {
            if vm.elements.isEmpty {
                Text("Nenhum registro de sincronização até o momento.")
                    .multilineTextAlignment(.leading)
            } else {
                ForEach(vm.elements, id: \.id) { registry in
                    SyncItemView(registry: registry)
                }
            }
        }
        .navigationTitle("Sincronização")
    }
}

struct SyncItemView: View {
    let registry: SyncRegistry
    
    var body: some View {
        VStack {
            HStack {
                VStack {
                    Text("Início")
                        .multilineTextAlignment(.center)
                        .font(.callout)
                    
                    Text(formattedDate(registry.start))
                        .multilineTextAlignment(.center)
                        .font(.footnote)
                }
                .frame(minWidth: 0, maxWidth: .infinity)
                
                VStack {
                    Text("Fim")
                        .multilineTextAlignment(.center)
                        .font(.callout)
                    
                    Text(formattedDate(kLong: registry.end))
                        .multilineTextAlignment(.center)
                        .font(.footnote)
                }
                .frame(minWidth: 0, maxWidth: .infinity)
                
            }
            
            HStack {
                Label(
                    title: { Text("Snowpiercer") },
                    icon: { Image(systemName: "gear") }
                )
                .labelStyle(.automatic)
                .font(.callout)
                .frame(minWidth: 0, maxWidth: .infinity)
                
                Label(
                    title: { Text("Concluído") },
                    icon: { Image(systemName: "arrow.triangle.2.circlepath") }
                )
                .font(.callout)
                .frame(minWidth: 0, maxWidth: .infinity)
            }
            .padding(.top, 2)
        }
        .alignmentGuide(.listRowSeparatorLeading) { _ in 0 }
    }
    
    func formattedDate(kLong: KotlinLong?) -> String {
        return formattedDate(kLong?.int64Value)
    }
    
    func formattedDate(_ millis: Int64?) -> String {
        guard let millis = millis else { return "--" }
        let date = Date(timeIntervalSince1970: TimeInterval(millis / 1000))
        return date.formatted(date: .numeric, time: .shortened)
    }
}

struct CustomLabel: LabelStyle {
    var spacing: Double = 0.0
    
    func makeBody(configuration: Configuration) -> some View {
        HStack(spacing: spacing) {
            configuration.icon
            configuration.title
        }
    }
}

#Preview {
    NavigationStack {
        SyncRegistryView()
    }
}
