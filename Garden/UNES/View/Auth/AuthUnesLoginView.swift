//
//  AuthUnesLoginView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 25/02/24.
//

import SwiftUI

struct AuthUnesLoginView: View {
    @Binding var path: NavigationPath
    @State var email = ""
    @State var code = ""
    @State var currentState: AuthState = .email
    
    enum AuthState: Hashable {
        case email, sendingEmail, code, sendingCode
    }
    
    var body: some View {
        ZStack {
            Rectangle()
                .fill(LinearGradient(colors: [.blue, .green, .orange], startPoint: .topLeading, endPoint: .bottomTrailing))
                .opacity(0.4)
                .blur(radius: 3.0)
                .ignoresSafeArea()
            
            ScrollView {
                VStack {
                    Spacer(minLength: 64)
                    Image(.hourglassStats)
                        .resizable()
                        .scaledToFit()
                    Spacer(minLength: 64)
                    Text("Conta UNES")
                        .font(.title)
                        .fontWeight(.light)
                        .padding(.horizontal)
                    
                    if currentState == .email || currentState == .sendingEmail {
                        Text("Acesse todas as experiencias compartilhadas do UNESVerso")
                            .fontWeight(.light)
                            .padding(.horizontal)
                    } else {
                        Text("Insira o código recebido no seu e-mail")
                            .fontWeight(.light)
                            .padding(.horizontal)
                    }
                    
                    if currentState == .email || currentState == .sendingEmail {
                        HStack {
                            Text("E-mail")
                                .font(.subheadline)
                                .fontWeight(.light)
                                .frame(width: 70, alignment: .leading)
                            Divider().frame(height: 15)
                            TextField("", text: $email)
                                .textFieldStyle(.automatic)
                                .disabled(currentState == .sendingEmail)
                                .keyboardType(.emailAddress)
                                .textInputAutocapitalization(.never)
                                .submitLabel(.go)
                                .onSubmit {
                                    
                                }
                        }
                        .padding()
                        .background(.background.opacity(0.7))
                        .clipShape(.buttonBorder)
                        .padding(.horizontal)
                        .padding(.top)
                    }
                    
                    if currentState == .code || currentState == .sendingCode {
                        HStack {
                            Text("Código")
                                .font(.subheadline)
                                .fontWeight(.light)
                                .frame(width: 70, alignment: .leading)
                            Divider().frame(height: 15)
                            TextField("", text: $code)
                                .textFieldStyle(.automatic)
                                .disabled(currentState == .sendingCode)
                                .keyboardType(.numberPad)
                                .textInputAutocapitalization(.never)
                                .submitLabel(.go)
                                .onSubmit {
                                    
                                }
                        }
                        .padding()
                        .background(.background.opacity(0.7))
                        .clipShape(.buttonBorder)
                        .padding(.horizontal)
                        .padding(.top)
                    }
                    
                    if currentState == .email {
                        Button(action: {
                            currentState = .sendingEmail
                        }, label: {
                            Text("Enviar código")
                                .padding(.horizontal)
                        })
                        .buttonStyle(.borderedProminent)
                        .padding(.horizontal)
                        .padding(.top)
                    }
                    
                    if currentState == .code {
                        Button(action: {
                            currentState = .sendingCode
                        }, label: {
                            Text("Confirmar código")
                                .padding(.horizontal)
                        })
                        .buttonStyle(.borderedProminent)
                        .padding(.horizontal)
                        .padding(.top)
                    }
                    
                    if currentState == .sendingEmail || currentState == .sendingCode {
                        ProgressView {
                            Text("Enviando")
                        }
                        .padding(.top)
                    }
                }
            }
        }
    }
}

#Preview {
    @State var path: NavigationPath = .init()
    return AuthUnesLoginView(path: $path)
}
