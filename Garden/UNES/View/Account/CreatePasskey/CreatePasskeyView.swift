//
//  CreatePasskeyView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 20/03/24.
//

import SwiftUI

@available(iOS 16.4, *)
struct CreatePasskeyView: View {
    @Binding var path: NavigationPath
    @Environment(\.authorizationController) private var authorizationController
    @StateObject private var vm = CreatePasskeyViewModel()
    
    var body: some View {
        GeometryReader { reader in
            ScrollView {
                VStack(spacing: 0) {
                    Image(.keysHandout)
                        .resizable()
                        .scaledToFit()
                        .frame(height: 150)
                        .padding(.horizontal, 56)
                        .padding(.top, 56)
                    
                    Text("Criar uma chave de acesso")
                        .font(.title)
                        .fontWeight(.medium)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 56)
                        .padding(.top, 8)
                    
                    Text("Crie uma chave de acesso para começar a fazer login usando apenas o rosto, a impressão digital ou o bloqueio de tela. Você pode criar uma chave de acesso neste dispositivo ou usar outro, como uma chave de segurança física.")
                        .font(.callout)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 48)
                        .padding(.top, 8)
                    
                    Spacer()
                    
                    Image(systemName: "person.badge.key.fill")
                        .foregroundStyle(.primary)
                    
                    Text("As chaves de acesso são uma alternativa simples e segura às senhas. As chaves de acesso oferecem a melhor proteção contra ameaças como o phishing.")
                        .font(.caption2)
                        .foregroundStyle(.secondary)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 48)
                        .padding(.top, 4)
                    
                    if !vm.loading {
                        Button {
                            Task { await vm.startPasskeyCreate(auth: authorizationController) }
                        } label: {
                            Text("Continuar")
                                .fontWeight(.medium)
                                .frame(maxWidth: .infinity)
                        }
                        .padding(.horizontal, 48)
                        .padding(.top)
                        .buttonStyle(.borderedProminent)
                        .controlSize(.large)
                        .clipShape(.rect(cornerRadius: 8))
                        .padding(.bottom)
                    } else {
                        ProgressView("Carregando informações")
                            .padding(.horizontal, 48)
                            .padding(.top)
                            .padding(.bottom)
                    }
                }.frame(minHeight: reader.size.height)
            }
        }
        .navigationTitle("Chave de acesso")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
        .alert("Erro ao criar chave", isPresented: $vm.showError) {
            Button {
                vm.showError = false
            } label: {
                Text("OK")
            }
        } message: {
            Text(vm.messageError)
        }.alert("Chave criada!", isPresented: $vm.completed) {
            Button {
                vm.completed = false
                path.removeLast()
            } label: {
                Text("Legal!")
            }
        } message: {
            Text("A partir de agora você pode usar a chave para acessar sua conta")
        }

    }
}

#Preview {
    @State var path: NavigationPath = .init()
    return NavigationStack(path: $path) {
        if #available(iOS 16.4, *) {
            CreatePasskeyView(path: $path)
        } else {
            ProgressView()
        }
    }
}
