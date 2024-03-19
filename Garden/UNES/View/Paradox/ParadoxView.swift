//
//  ParadoxView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 19/03/24.
//

import SwiftUI

struct ParadoxView: View {
    var body: some View {
        VStack {
            Spacer()
            Image(.underDevelopment)
                .resizable()
                .scaledToFit()
            
            Text("O Paradoxo está confuso e não há como atravessar de maneira segura")
                .multilineTextAlignment(.center)
            Spacer()
            Text("Esta funcionalidade será apresentada numa atualização futura.\nPrepare-se para ver a média de todas as disciplinas e professores com dados históricos que conseguem dobrar o tempo e o espaço")
                .multilineTextAlignment(.center)
                .font(.caption2)
                .foregroundStyle(.secondary)
        }
        .padding()
        .navigationTitle("Paradoxo de Zhonyas")
    }
}

#Preview {
    NavigationStack {
        ParadoxView()
    }
}
