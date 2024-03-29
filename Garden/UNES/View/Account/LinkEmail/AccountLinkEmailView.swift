//
//  AccountLinkEmailView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 20/03/24.
//

import SwiftUI

struct AccountLinkEmailView: View {
    @Binding var path: NavigationPath
    @StateObject private var vm: AccountLinkEmailViewModel = .init()
    @State private var email = ""
    
    var body: some View {
        Form {
            Section {
                HStack {
                    Text("Email")
                        .font(.subheadline)
                        .frame(width: 56, alignment: .leading)
                    Divider().frame(height: 15)
                    TextField("", text: $email)
                        .textFieldStyle(.automatic)
                        .textContentType(.emailAddress)
                        .disabled(vm.loading)
                        .autocorrectionDisabled()
                        .textInputAutocapitalization(.never)
                        .submitLabel(.done)
                }
                .clipShape(.rect(cornerRadius: 8))
            } header: {
                VStack(spacing: 0) {
                    Image(.mailIllustration)
                        .resizable()
                        .scaledToFit()
                        .frame(height: 220)
                    
                    Text("Vamos adicionar um email")
                        .textCase(.none)
                        .font(.body)
                        .multilineTextAlignment(.center)
                        .foregroundStyle(.foreground)
                    
                    Text("Esta etapa garante que você conseguirá acessar ou recuperar esta mesma conta no futuro")
                        .font(.callout)
                        .textCase(.none)
                        .foregroundStyle(.foreground)
                        .multilineTextAlignment(.center)
                        .padding(.top, 16)
                }
                .padding(.bottom, 16)
            } footer: {
                VStack {
                    Text("A conta UNES não substitui sua conta do Portal. Seu email não será vinculado à instituição de ensino.")
                        .font(.caption2)
                    
                    if vm.loading {
                        ProgressView("Carregando")
                            .padding(.top, 16)
                    }
                }
            }
            
            Section {
                if !vm.loading {
                    Button {
                        vm.register(email: email)
                    } label: {
                        Text("Confirmar")
                    }
                }
            }
        }
        .navigationTitle("Email")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
        .onChange(of: vm.securityCode) { newValue in
            if !newValue.isEmpty {
                path.append(EmailConfirmationAccFlow(security: newValue))
                vm.securityCode = ""
            }
        }
        .navigationDestination(for: EmailConfirmationAccFlow.self) { item in
            EmailAccountCodeView(securityCode: item.security, path: $path)
        }
    }
}

#Preview {
    @State var path: NavigationPath = .init()
    return NavigationStack(path: $path) {
        AccountLinkEmailView(path: $path)
    }
}
