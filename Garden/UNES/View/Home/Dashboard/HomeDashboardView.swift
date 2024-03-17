//
//  HomeDashboardView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 16/03/24.
//

import SwiftUI
import SDWebImageSwiftUI

struct HomeDashboardView: View {
    let selectSchedule: () -> Void
    let selectMessages: () -> Void
    
    
    var name = "João"
    var message = "Caros, ja disponibilizei uma previa da nota de participacao de voces aqui no Sagres. Elas podem sofrer alteracoes caso alguem entregue algum trabalho para ajudar na nota ou precise de poucos decimos para passar direto sem prova final. Lembrem de trazer folhas para responderem as questoes dissertativas na prova. Bom estudos e ate la!"
    
    var body: some View {
        VStack {
            HStack {
                VStack(alignment: .leading) {
                    Text("Olá, \(name)")
                        .font(.title3)
                        .fontWeight(.medium)
                    Text("Engenharia de Computação")
                        .font(.footnote)
                        .fontWeight(.regular)
                }
                Spacer()
                WebImage(url: URL(string: "https://i.imgur.com/iMSxR7H.jpg")) { image in
                    image.resizable()
                } placeholder: {
                    Rectangle().foregroundColor(.gray)
                }
                .indicator(.activity)
                .scaledToFit()
                .transition(.fade(duration: 0.5))
                .frame(width: 48, height: 48)
                .clipShape(.circle)
            }
            .padding(.horizontal)
            .padding(.top)
            
            ScrollView(.vertical) {
                VStack {
                    VStack(alignment: .leading) {
                        Text("Você deveria estar em...")
                            .foregroundStyle(.white)
                            .font(.title2)
                            .fontWeight(.light)
                            .padding()
                        
                        VStack(alignment: .leading) {
                            Text("Métodologia da Pesquisa e Desenvolvimento em Engenharia de Computação")
                                .multilineTextAlignment(.leading)
                                .foregroundStyle(.white)
                                .font(.system(size: 30, weight: .regular, design: .serif))
                                .font(.title)
                                .fontWeight(.medium)
                        }
                        .padding(.horizontal)
                        .frame(height: 300, alignment: .bottomLeading)
                        
                        Text("07:30 ~ 09:30")
                            .foregroundStyle(.white)
                            .font(.headline)
                            .fontWeight(.regular)
                            .padding(.horizontal)
                        
                        Text("MT 55 - Módulo 5 - UEFS")
                            .foregroundStyle(.white)
                            .font(.headline)
                            .fontWeight(.regular)
                            .padding(.horizontal)
                        
                        Text("Começa em 16 minutos")
                            .foregroundStyle(.white)
                            .font(.subheadline)
                            .fontWeight(.regular)
                            .padding(.horizontal)
                        
                        HStack {
                            Spacer()
                            Button(action: {
                                selectSchedule()
                            }, label: {
                                Text("Ver todas as aulas")
                                    .foregroundStyle(.white)
                            })
                        }.padding()
                    }
                    .background(content: {
                        Rectangle()
                            .foregroundColor(.indigo.opacity(0.8))
                    })
                    .background(content: {
                        WebImage(url: URL(string:"https://cdn.dribbble.com/users/418188/screenshots/6665427/design_for_education_illustration_tubik.png")) { image in
                            image.resizable()
                        } placeholder: {
                            Rectangle().foregroundColor(.gray)
                        }
                        .scaledToFill()
                    })
                    .clipShape(.rect(cornerRadius: 16))
                    .shadow(color: .indigo, radius: 8)
                    .padding(.horizontal)
                    .padding(.top)
                    
                    VStack(alignment: .leading) {
                        Text("Rosária da Paixão Trindade")
                            .padding(.horizontal)
                            .padding(.top)
                            .fontWeight(.light)
                        
                        Text("Metodologia do Trabalho Cientifico")
                            .padding(.horizontal)
                            .font(.footnote)
                            .fontWeight(.light)
                        
                        Text(message)
                            .multilineTextAlignment(.leading)
                            .padding(.horizontal)
                            .padding(.bottom)
                            .padding(.top, 2)
                            .font(.body)
                            .fontWeight(.regular)
                    }
                    .background(content: {
                        Rectangle()
                            .foregroundColor(.blue.opacity(0.2))
                    })
                    .clipShape(.rect(cornerRadius: 16))
                    .padding(.horizontal)
                    .padding(.top)
                    .padding(.bottom)
                }
            }
        }
    }
}

#Preview {
    HomeDashboardView {
        
    } selectMessages: {
        
    }
}
