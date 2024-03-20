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
        VStack(spacing: 0) {
            Image(.mailIllustration)
                .resizable()
                .scaledToFit()
                .frame(height: 220)
            
            Text("Vamos adicionar um email")
            Text("Esta etapa garante que você conseguirá acessar ou recuperar esta mesma conta no futuro")
                .font(.callout)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 48)
                .padding(.top, 16)
            
            Spacer()
            
            HStack {
                Text("Email")
                    .font(.subheadline)
                    .fontWeight(.light)
                    .frame(width: 70, alignment: .leading)
                Divider().frame(height: 15)
                TextField("", text: $email)
                    .textFieldStyle(.automatic)
                    .textContentType(.emailAddress)
                    .disabled(vm.loading)
                    .autocorrectionDisabled()
                    .textInputAutocapitalization(.never)
                    .submitLabel(.done)
            }
            .padding()
            .background(Color(UIColor.secondarySystemGroupedBackground).opacity(0.7))
            .clipShape(.rect(cornerRadius: 8))
            .padding(.horizontal)
        }
        .background(Color(uiColor: .systemGroupedBackground))
        .navigationTitle("Email")
        .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview {
    @State var path: NavigationPath = .init()
    return NavigationStack(path: $path) {
        AccountLinkEmailView(path: $path)
    }
}
