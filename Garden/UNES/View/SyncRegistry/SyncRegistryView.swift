//
//  SyncRegistryView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 19/03/24.
//

import SwiftUI

struct SyncRegistryView: View {
    var body: some View {
        List {
            SyncItemView()
            SyncItemView()
        }
        .navigationTitle("Sincronização")
    }
}

struct SyncItemView: View {
    var body: some View {
        VStack {
            HStack {
                VStack {
                    Text("Início")
                        .multilineTextAlignment(.center)
                        .font(.callout)
                    
                    Text("18/03/2024 13:45:12")
                        .multilineTextAlignment(.center)
                        .font(.footnote)
                }
                .frame(minWidth: 0, maxWidth: .infinity)
                
                VStack {
                    Text("Fim")
                        .multilineTextAlignment(.center)
                        .font(.callout)
                    
                    Text("18/03/2024 13:45:12")
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
