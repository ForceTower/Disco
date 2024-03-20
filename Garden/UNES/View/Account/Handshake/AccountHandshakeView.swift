//
//  AccountHandshakeView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 19/03/24.
//

import SwiftUI

struct AccountHandshakeView: View {
    @Binding var path: NavigationPath
    var body: some View {
        GeometryReader { reader in
            ScrollView {
                VStack {
                    Image(.hourglassLanding)
                        .resizable()
                        .scaledToFit()
                        .frame(height: 220)
                    Text("Nesta etapa, o UNES irá enviar suas credenciais para verificar se a sua conta do Portal é valida e permitir seu acesso ao UNESVerso")
                        .font(.callout)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 48)
                        .padding(.top, 32)
                    
                    Spacer()
                    
                    Text("Ao continuar, você concorda com os Termos de Uso e Política de Privacidade do UNES")
                        .font(.caption2)
                        .foregroundStyle(.secondary)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 48)
                    
                    Button {
                        
                    } label: {
                        Text("Continuar")
                            .fontWeight(.medium)
                            .frame(maxWidth: .infinity)
                    }
                    .buttonStyle(.borderedProminent)
                    .padding(.horizontal, 48)
                    .controlSize(.large)
                    
                    Button {
                        
                    } label: {
                        Label {
                            Text("Continuar com Chave Senha")
                                .fontWeight(.medium)
                        } icon: {
                            Image(systemName: "person.badge.key.fill")
                        }
                        .frame(maxWidth: .infinity)
                    }
                    .buttonStyle(.borderedProminent)
                    .controlSize(.large)
                    .padding(.horizontal, 48)
                    .padding(.bottom, 16)
                }.frame(minHeight: reader.size.height)
            }
        }
        .navigationTitle("Login")
        .toolbar(.hidden, for: .tabBar)
    }
}

#Preview {
    @State var path: NavigationPath = .init()
    return NavigationStack(path: $path) {
        AccountHandshakeView(path: $path)
    }
}
