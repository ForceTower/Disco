//
//  AuthPortalLoginView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 25/02/24.
//

import SwiftUI

struct AuthPortalLoginView: View {
    enum FocusableField: Hashable {
        case username, password
    }
    
    @Binding var path: NavigationPath
    @State var username = ""
    @State var password = ""
    @FocusState private var focusedField: FocusableField?
    
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
                    .clipShape(.buttonBorder)
                    .padding(.horizontal)
                    
                    HStack {
                        Text("Senha")
                            .font(.subheadline)
                            .fontWeight(.light)
                            .frame(width: 70, alignment: .leading)
                        Divider().frame(height: 15)
                        SecureField("", text: $password)
                            .textFieldStyle(.automatic)
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
                    .clipShape(.buttonBorder)
                    .padding(.horizontal)
                }.formStyle(.columns).onAppear(perform: {
                    focusedField = .username
                })
                
                Button(action: {
                    path.append("StartPortalAuth")
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
                    path.append("UnesAuth")
                }, label: {
                    Text("Acessar usando conta UNES")
                        .padding(.horizontal)
                })
                .buttonStyle(.borderedProminent)
                .controlSize(.regular)
            }
        }
    }
}

#Preview {
    @State var path: NavigationPath = .init()
    return AuthPortalLoginView(path: $path)
}
