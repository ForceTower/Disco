//
//  AccountView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 19/03/24.
//

import SwiftUI
import PhotosUI
import Club
import SDWebImageSwiftUI

struct AccountView: View {
    @Binding var path: NavigationPath
    @StateObject private var vm: AccountViewModel = .init()
    @State private var showConnectSplash = false
    
    var body: some View {
        List {
            Section {
                if let account = vm.currentAccount {
                    if let email = account.email, !email.isEmpty {
                        if #available(iOS 16.4, *) {
                            NavigationLink {
                                CreatePasskeyView(path: $path)
                            } label: {
                                Label {
                                    Text("Criar chave senha")
                                } icon: {
                                    Image(systemName: "person.badge.key.fill")
                                        .foregroundStyle(.foreground)
                                }
                            }
                            
                            NavigationLink {
                                
                            } label: {
                                Text("Ver chaves")
                            }
                            VStack(alignment: .leading) {
                                Text("Email registrado")
                                Text(email)
                                    .font(.callout)
                            }
                        }
                    } else {
                        NavigationLink {
                            AccountLinkEmailView(path: $path)
                        } label: {
                            Label {
                                Text("Verificar seu email")
                            } icon: {
                                Image(systemName: "envelope")
                                    .foregroundStyle(.foreground)
                            }
                        }
                    }
                } else {
                    Button {
                        showConnectSplash = true
                    } label: {
                        Text("Acessar conta UNES")
                    }
                }
            } header: {
                if let profile = vm.currentProfile {
                    AccountHeaderView(
                        profile: profile,
                        account: vm.currentAccount,
                        imageSelection: $vm.imageSelection
                    ) {
                        
                    }
                } else {
                    ProgressView()
                }
            } footer: {
                if vm.currentAccount == nil {
                    Text("Você não está conectado a uma conta UNES no momento.")
                }
            }
            
            if vm.currentAccount?.email != nil {
//                Section("Premium") {
//                    NavigationLink(value: 2) {
//                        Text("Comprar UNES Pro")
//                    }
//                }
            }
        }
        .sheet(isPresented: $showConnectSplash) {
            LoginAccountSplash {
                showConnectSplash = false
                path.append(HandshakeAccFlow())
            }
        }
        .onChange(of: vm.currentAccount) { connected in
            if connected == nil {
                showConnectSplash = true
            }
        }
        .navigationDestination(for: HandshakeAccFlow.self) { _ in
            AccountHandshakeView(path: $path)
        }
        .navigationTitle("Conta")
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct AccountHeaderView: View {
    let profile: Profile
    let account: ServiceAccount?
    @Binding var imageSelection: PhotosPickerItem?
    
    let onChangeProfilePicture: () -> Void
    
    var body: some View {
        HStack {
            Spacer()
            VStack(alignment: .center, spacing: 8) {
                PhotosPicker(selection: $imageSelection,
                             matching: .images,
                             photoLibrary: .shared()) {
                    if let image = account?.imageUrl {
                        WebImage(url: URL(string: image)) { image in
                            image.resizable()
                        } placeholder: {
                            Rectangle().foregroundColor(.gray)
                        }
                        .indicator(.activity)
                        .scaledToFit()
                        .transition(.fade(duration: 0.5))
                        .frame(width: 100, height: 100)
                        .clipShape(.circle)
                    } else {
                        ZStack {
                            Image(systemName: "person.crop.circle")
                                .resizable()
                                .scaledToFit()
                                .foregroundStyle(.secondary)
                                .frame(width: 100, height: 100)
                                .clipShape(.circle)
                            
                            Image(systemName: "pencil.circle.fill")
                                .symbolRenderingMode(.multicolor)
                                .font(.system(size: 30))
                                .foregroundColor(.accentColor)
                                .opacity(0.8)
                        }
                    }
                }.buttonStyle(.borderless)

                Text(account?.name ?? profile.name ?? "????")
                    .textInputAutocapitalization(.never)
                    .font(.body)
                    .foregroundStyle(.foreground)
                    .textCase(.none)
                
                if let email = account?.email, !email.isEmpty {
                    Label {
                        Text("Sua conta está verificada")
                            .textCase(.none)
                            .foregroundStyle(.foreground)
                    } icon: {
                        Image(systemName: "checkmark.seal.fill")
                            .foregroundStyle(.blue)
                    }
                }
                
            }
            Spacer()
        }
        .padding(.bottom)
    }
}

struct LoginAccountSplash: View {
    let onContinue: () -> Void
    
    var body: some View {
        VStack(spacing: 0) {
            Image(.coloredLogo)
                .resizable()
                .scaledToFit()
                .frame(height: 150)
                .padding(.horizontal, 56)
                .padding(.top, 56)
            
            Text("Boas vindas à conta UNES")
                .font(.title)
                .fontWeight(.medium)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 56)
                .padding(.top, 8)
            
            Text("Com a conta UNES você tem acesso aos mais diversos recursos disponíveis no aplicativo, como historico de disciplinas e muito mais.")
                .font(.callout)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 48)
                .padding(.top, 8)
            
            Spacer()
            
            Image(systemName: "person.and.background.dotted")
                .symbolRenderingMode(.multicolor)
                .foregroundStyle(.blue)
            
            Text("As suas credenciais de acesso ao Portal serão usadas para processar o seu login e garantir sua identidade. O processo é efêmero, as credenciais não são salvas nos servidores do UNES. Para mais informações, o código pode ser inspecionado [aqui](https://github.com/ForceTower/Disco)")
                .font(.caption2)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 48)
                .padding(.top, 4)
            
            Button {
                onContinue()
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
        }
    }
}

#Preview {
    LoginAccountSplash {
        
    }
}

#Preview {
    @State var path: NavigationPath = .init()
    return NavigationStack(path: $path) {
        AccountView(path: $path)
    }
}
