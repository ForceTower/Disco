//
//  AuthWelcomeView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 25/02/24.
//

import SwiftUI

struct AuthWelcomeView: View {
    @Binding var path: NavigationPath
    
    var body: some View {
        ZStack {
            Rectangle()
                .fill(LinearGradient(colors: [.blue, .green, .orange], startPoint: .topLeading, endPoint: .bottomTrailing))
                .opacity(0.4)
                .blur(radius: 3.0)
                .ignoresSafeArea()
        
            VStack {
                Spacer()
                
                Image(.classroomWorking)
                    .resizable(resizingMode: .stretch)
                    .scaledToFit()
                
                Spacer()
                
                Text("Boas vindas ao UNES")
                
                Text("A maneira mais facil de acessar o Portal do Aluno")
                    .font(.caption)
                    .fontWeight(.light)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)
                
                Button(action: {
                    path.append("PortalAuth")
                }, label: {
                    Text("Vamos lá")
                        .frame(width: 150)
                }).buttonStyle(.borderedProminent)
                    .padding(.top, 8)
                    .padding(.horizontal)
                
                Text("Ao acessar voce concorda com os Termos de Uso do UNES")
                    .font(.caption2)
                    .fontWeight(.light)
                    .multilineTextAlignment(.center)
                    .padding(.top, 4)
                    .padding(.bottom, 8)
                    .padding(.horizontal)
            }
        }
    }
}

#Preview {
    @State var path: NavigationPath = .init()
    return AuthWelcomeView(path: $path)
}
