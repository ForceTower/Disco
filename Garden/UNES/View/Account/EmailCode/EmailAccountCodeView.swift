//
//  EmailAccountCodeView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 20/03/24.
//

import SwiftUI

struct EmailAccountCodeView: View {
    let securityCode: String
    @Binding var path: NavigationPath
    @StateObject private var vm = EmailAccountCodeViewModel()
    @State var code = ""
    
    var body: some View {
        Form {
            Section {
                HStack {
                    Text("Código")
                        .font(.subheadline)
                        .frame(width: 56, alignment: .leading)
                    Divider().frame(height: 15)
                    TextField("", text: $code)
                        .textFieldStyle(.automatic)
                        .keyboardType(.numberPad)
                        .textContentType(.oneTimeCode)
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
                    
                    Text("Estamos validando seu email")
                        .textCase(.none)
                        .font(.body)
                        .multilineTextAlignment(.center)
                        .foregroundStyle(.foreground)
                    
                    Text("Você receberá um código de confirmação no email informado")
                        .font(.callout)
                        .textCase(.none)
                        .foregroundStyle(.foreground)
                        .multilineTextAlignment(.center)
                        .padding(.top, 16)
                }
                .padding(.bottom, 16)
            } footer: {
                VStack {
                    Text("O código tem duração máxima de alguns minutos")
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
                        vm.confirm(code: code, security: securityCode)
                    } label: {
                        Text("Confirmar")
                    }
                }
            }
            
            if vm.showResend {
                Section {
                    Button(role: .destructive) {
                        path.removeLast()
                    } label: {
                        Text("Reenviar email")
                    }
                } footer: {
                    VStack {
                        Text("O código anterior deixará de funcionar")
                            .font(.caption2)
                    }
                }
            }
        }
        .navigationTitle("Verificar email")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
        .alert(vm.errorTitle, isPresented: $vm.showError) {
            Button {
                vm.showError = false
            } label: {
                Text("OK")
            }
        } message: {
            Text(vm.errorSubtitle)
        }
        .onChange(of: vm.completed) { newValue in
            if newValue {
                path.removeLast(path.count - 1)
            }
        }
        .onAppear {
            vm.startCount()
        }
    }
}

#Preview {
    @State var path: NavigationPath = .init()
    return NavigationStack(path: $path) {
        EmailAccountCodeView(securityCode: "sec_eyrsiufsgfyyeru", path: $path)
    }
}
