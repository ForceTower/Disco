//
//  AuthPortalLoginView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 25/02/24.
//

import SwiftUI
import Club

struct AuthPortalLoginView: View {
    enum FocusableField: Hashable {
        case username, password
    }
    
    @Environment(\.authorizationController) private var authorizationController
    @EnvironmentObject private var router: RootRouter
    
    @Binding var path: NavigationPath
    @State var username = ""
    @State var password = ""
    @FocusState private var focusedField: FocusableField?
    @StateObject private var viewModel: AuthPortalLoginViewModel = .init(attestationUseCase: AppDIContainer.shared.resolve(), loginUseCase: AppDIContainer.shared.resolve())
    
    var body: some View {
        ZStack {
            Rectangle()
                .fill(LinearGradient(colors: [.blue, .green, .orange], startPoint: .topLeading, endPoint: .bottomTrailing))
                .opacity(0.4)
                .blur(radius: 3.0)
                .ignoresSafeArea()
            
            VStack {
                Spacer()
                
                Image(.coloredLogo)
                    .resizable()
                    .scaledToFit()
                    .frame(height: 240)
                
                Spacer()
                
                Text("Insira os dados de login do Portal")
                    .font(.title3)
                    .fontWeight(.light)
                
                Form {
                    HStack {
                        Text("Usuário")
                            .font(.subheadline)
                            .fontWeight(.light)
                            .frame(width: 70, alignment: .leading)
                        Divider().frame(height: 15)
                        TextField("", text: $username)
                            .textFieldStyle(.automatic)
                            .textContentType(.username)
                            .autocorrectionDisabled()
                            .textInputAutocapitalization(.never)
                            .focused($focusedField, equals: .username)
                            .submitLabel(.next)
                            .onSubmit {
                                focusedField = .password
                            }
                    }
                    .padding()
                    .background(.background.opacity(0.7))
                    .clipShape(.rect(cornerRadius: 8))
                    .padding(.horizontal)
                    
                    HStack {
                        Text("Senha")
                            .font(.subheadline)
                            .fontWeight(.light)
                            .frame(width: 70, alignment: .leading)
                        Divider().frame(height: 15)
                        SecureField("", text: $password)
                            .textFieldStyle(.automatic)
                            .textContentType(.password)
                            .autocorrectionDisabled()
                            .textInputAutocapitalization(.never)
                            .submitLabel(.done)
                            .focused($focusedField, equals: .password)
                            .onSubmit {
                                focusedField = nil
                            }
                    }
                    .padding()
                    .background(.background.opacity(0.7))
                    .clipShape(.rect(cornerRadius: 8))
                    .padding(.horizontal)
                }.formStyle(.columns)
                
                Button(action: {
                    Task { await login() }
                }, label: {
                    Text("Entrar")
                        .padding(.horizontal)
                })
                .buttonStyle(.borderedProminent)
                .controlSize(.regular)
                .padding(.top)
                
                HStack {
                    Rectangle()
                        .frame(width: 100, height: 1)
                        .foregroundColor(.white.opacity(0.7))
                    Text("Ou")
                        .font(.caption2)
                        .fontWeight(.light)
                        .padding(.horizontal)
                    Rectangle()
                        .frame(width: 100, height: 1)
                        .foregroundColor(.white.opacity(0.7))
                }
                .padding(.vertical, 4)
                
                Button(action: {
                    Task { await signIn() }
                }, label: {
                    Label(
                        title: { 
                            Text("Entrar usando Chave Senha")
                            .padding(.horizontal)
                        },
                        icon: {
                            Image(systemName: "person.badge.key.fill")
                        }
                    )
                })
                .buttonStyle(.borderedProminent)
                .controlSize(.regular)
            }
        }.alert(viewModel.errorTitle, isPresented: $viewModel.showLoginError) {
            Button("OK") {
                viewModel.showLoginError = false
            }
        } message: {
            Text(viewModel.errorDescription)
        }.onAppear(perform: {
            viewModel.router = router
        })
    }
    
    func login() async {
        viewModel.login(username: username, password: password)
    }
    
    func signIn() async {
        await viewModel.startAttestation(controller: authorizationController)
    }
}

#Preview {
    @State var path: NavigationPath = .init()
    return AuthPortalLoginView(path: $path).environmentObject(RootRouter())
}
