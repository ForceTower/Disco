//
//  AccountHandshakeView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 19/03/24.
//

import SwiftUI
import AuthenticationServices

struct AccountHandshakeView: View {
    @Binding var path: NavigationPath
    @StateObject private var vm: AccountHandshakeViewModel = .init()
    
    var body: some View {
        GeometryReader { reader in
            ScrollView {
                VStack {
                    Image(.hourglassLanding)
                        .resizable()
                        .scaledToFit()
                        .frame(height: 220)
                    
                    Text("Nesta etapa, o UNES irá enviar suas credenciais para validar a sua conta do Portal e permitir seu acesso ao UNESVerso")
                        .font(.callout)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 48)
                        .padding(.top, 32)
                    
                    Spacer()
                    
                    if !vm.loading {
                        VStack {
                            Text("Ao continuar, você concorda com os Termos de Uso e Política de Privacidade do UNES")
                                .font(.caption2)
                                .foregroundStyle(.secondary)
                                .multilineTextAlignment(.center)
                                .padding(.horizontal, 48)
                            
                            Button {
                                vm.handshake()
                            } label: {
                                Text("Continuar")
                                    .fontWeight(.medium)
                                    .frame(maxWidth: .infinity)
                            }
                            .buttonStyle(.borderedProminent)
                            .padding(.horizontal, 48)
                            .controlSize(.large)
                            
                            if #available(iOS 16.4, *) {
                                AccountPasskeyLoginView { auth in
                                    
                                }
                            }
                        }
                    } else {
                        ProgressView()
                        Spacer()
                    }
                }.frame(minHeight: reader.size.height)
            }
        }
        .navigationTitle("Login")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
        .onChange(of: vm.completed) { newValue in
            if newValue {
                vm.completed = false
                path.append(EmailAccFlow())
            }
        }
        .onChange(of: vm.finished) { newValue in
            if newValue {
                vm.finished = false
                path.removeLast(path.count - 1)
            }
        }
        .navigationDestination(for: EmailAccFlow.self) { _ in
            AccountLinkEmailView(path: $path)
        }
        .alert(vm.titleError, isPresented: $vm.showError) {
            Button {
                vm.showError = false
            } label: {
                Text("OK")
            }
        } message: {
            Text(vm.messageError)
        }
    }
}

@available(iOS 16.4, *)
struct AccountPasskeyLoginView: View {
    @Environment(\.authorizationController) private var authorizationController
    let doLogin: (AuthorizationController) -> Void
    
    var body: some View {
        Button {
            doLogin(authorizationController)
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
    }
}

#Preview {
    @State var path: NavigationPath = .init()
    return NavigationStack(path: $path) {
        AccountHandshakeView(path: $path)
    }
}
