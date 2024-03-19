//
//  AccountView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 19/03/24.
//

import SwiftUI
import Club
import SDWebImageSwiftUI

struct AccountView: View {
    @StateObject private var vm: AccountViewModel = .init()
    
    var body: some View {
        List {
            Section {
                NavigationLink(value: 1) {
                    Label {
                        Text("Criar chave senha")
                    } icon: {
                        Image(systemName: "person.badge.key.fill")
                            .foregroundStyle(.foreground)
                    }
                }
                NavigationLink(value: 2) {
                    Text("Ver chaves")
                }
                if let email = vm.currentProfile?.email, !email.isEmpty {
                    VStack(alignment: .leading) {
                        Text("Email registrado")
                        Text(email)
                            .font(.callout)
                    }
                }
            } header: {
                if let profile = vm.currentProfile {
                    AccountHeaderView(profile: profile)
                } else {
                    ProgressView()
                }
            }
            
            Section("Premium") {
                NavigationLink(value: 2) {
                    Text("Comprar UNES Pro")
                }
            }
        }
        .navigationDestination(for: Int.self) { item in
            
        }
        .navigationTitle("Conta")
    }
}

struct AccountHeaderView: View {
    let profile: Profile
    
    var body: some View {
        HStack {
            Spacer()
            VStack(alignment: .center, spacing: 8) {
                Button {
                    
                } label: {
                    Label {
                        Text("Trocar foto")
                    } icon: {
                        if let image = profile.imageUrl {
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
                            Image(systemName: "person.crop.circle")
                                .resizable()
                                .scaledToFit()
                                .foregroundStyle(.secondary)
                                .frame(width: 100, height: 100)
                                .clipShape(.circle)
                        }
                    }
                    .labelStyle(.iconOnly)
                }
                
                Text(profile.name ?? "????")
                    .textInputAutocapitalization(.never)
                    .font(.body)
                    .foregroundStyle(.foreground)
                    .textCase(.none)
                
                if let email = profile.email, !email.isEmpty {
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

#Preview {
    NavigationStack {
        AccountView()
    }
}
