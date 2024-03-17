//
//  HomeDashboardView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 16/03/24.
//

import SwiftUI
import Club
import SDWebImageSwiftUI

struct HomeDashboardView: View {
    let selectSchedule: () -> Void
    let selectMessages: () -> Void
    
    @StateObject private var vm: HomeDashboardViewModel = .init(schedule: AppDIContainer.shared.resolve(), messages: AppDIContainer.shared.resolve(), user: AppDIContainer.shared.resolve())
    
    
    var name = "João"
    
    var body: some View {
        VStack {
            HStack {
                VStack(alignment: .leading) {
                    if let name = vm.currentProfile?.name {
                        Text("Olá, \(name.split(separator: " ")[0])")
                            .font(.title3)
                            .fontWeight(.medium)
                        Text("Universidade Estadual de Feira de Santana")
                            .font(.footnote)
                            .fontWeight(.regular)
                    }
                }
                Spacer()
                if let imageUrl = vm.currentProfile?.imageUrl {
                    WebImage(url: URL(string: imageUrl)) { image in
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
            }
            .padding(.horizontal)
            .padding(.top)
            
            ScrollView(.vertical) {
                VStack {
                    CurrentClassCardView(item: vm.currentClass, selectSchedule: selectSchedule)
                    
                    if let message = vm.latestMessage {
                        LatestPlatformMessageCardView(message: message)
                    }
                }
            }
        }
    }
}

struct CurrentClassCardView: View {
    let item: ExtendedClassLocationData?
    let selectSchedule: () -> Void
    
    var body: some View {
        VStack(alignment: .leading) {
            
            Text(nextClassIndicator())
                .foregroundStyle(.white)
                .font(.title2)
                .fontWeight(.light)
                .padding()
            
            VStack(alignment: .leading) {
                Text(className())
                    .multilineTextAlignment(.leading)
                    .foregroundStyle(.white)
                    .font(.system(size: 30, weight: .regular, design: .serif))
                    .font(.title)
                    .fontWeight(.medium)
            }
            .padding(.horizontal)
            .frame(height: 300, alignment: .bottomLeading)
            
            if let location = item?.ref.location {
                Text("\(location.startsAt) - \(location.endsAt)")
                    .foregroundStyle(.white)
                    .font(.headline)
                    .fontWeight(.regular)
                    .padding(.horizontal)
            }
            
            if let location = classLocation() {
                Text(location)
                    .foregroundStyle(.white)
                    .font(.headline)
                    .fontWeight(.regular)
                    .padding(.horizontal)
            }
            
            if let difference = classStartedDiff() {
                Text(difference)
                    .foregroundStyle(.white)
                    .font(.subheadline)
                    .fontWeight(.regular)
                    .padding(.horizontal)
            }
            
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
                .foregroundColor(classColorOverlay().opacity(0.8))
        })
        .background(content: {
            WebImage(url: URL(string: classImage())) { image in
                image.resizable()
            } placeholder: {
                Rectangle().foregroundColor(.gray)
            }
            .scaledToFill()
        })
        .clipShape(.rect(cornerRadius: 16))
        .shadow(color: classColorOverlay(), radius: 8)
        .padding(.horizontal)
        .padding(.top)
    }
    
    func nextClassIndicator() -> String {
        guard let element = item else { return "Por hoje acabou" }
        
        if element.currentClass {
            return "Você deveria estar em..."
        } else {
            return "Próxima aula"
        }
    }
    
    func className() -> String {
        guard let element = item else {
            return "Você não tem mais aulas hoje! Que demais!"
        }
        return WordUtils.shared.toTitleCase(str: element.ref.discipline.name)
    }
    
    func classLocation() -> String? {
        guard let location = item?.ref.location else { return nil }
        
        let room = location.room?.uppercased()
        let modulo = location.modulo?.lowercased().localizedCapitalized
        let campus = location.campus?.uppercased()
        
        let values = [room, modulo, campus].compactMap { $0 }.joined(separator: " - ")
        if values.isEmpty { return nil }
        return values
    }
    
    func classStartedDiff() -> String? {
        guard let difference = item?.differenceMin?.int64Value else { return nil }
        switch difference {
        case 0: 
            return "Acabou de começar"
        case 1...60: 
            return "Começa em \(difference) minutos"
        case -30 ... -1:
            return "Comecou \(abs(difference)) minutos atrás"
        default: 
            return nil
        }
    }
    
    func classImage() -> String {
        guard let element = item else {
            return "https://cdn.dribbble.com/users/851350/screenshots/4314097/vacation.png"
        }
        
        if element.currentClass {
            return "https://cdn.dribbble.com/users/2330950/screenshots/6232237/59_2x.jpg"
        } else {
            return "https://cdn.dribbble.com/users/418188/screenshots/6665427/design_for_education_illustration_tubik.png"
        }
    }
    
    func classColorOverlay() -> Color {
        if item != nil { return .indigo }
        return .init(hex: "ff6c00")
    }
}

struct LatestPlatformMessageCardView: View {
    let message: Message
    
    var body: some View {
        VStack(alignment: .leading) {
            Text(disciplineName())
                .padding(.horizontal)
                .padding(.top)
                .fontWeight(.light)
            
            if let name = senderName() {
                Text(name)
                    .padding(.horizontal)
                    .font(.footnote)
                    .fontWeight(.light)
            }
            
            Text(message.content)
                .multilineTextAlignment(.leading)
                .padding(.horizontal)
                .padding(.top, 2)
                .font(.body)
                .fontWeight(.regular)
            
            HStack {
                Spacer()
                
                Text(messageSent())
                    .font(.caption)
                    .fontWeight(.regular)
                    .foregroundStyle(.gray)
                    .padding(.top, 2)
                    .padding(.trailing)
                    .padding(.bottom)
            }
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
    
    func disciplineName() -> String {
        var discipline = message.discipline
        if discipline == nil && message.senderProfile == 3 {
            discipline = "Secretaria Acadêmica"
        }
        
        let text = discipline ?? message.senderName ?? "Desconhecido"
        return WordUtils.shared.toTitleCase(str: text)
    }
    
    func senderName() -> String? {
        var discipline = message.discipline
        if discipline == nil && message.senderProfile == 3 {
            discipline = "Secretaria Acadêmica"
        }
        
        if discipline == nil {
            return nil
        }
        
        let text = message.senderName ?? "????"
        return WordUtils.shared.toTitleCase(str: text)
    }
    
    func messageSent() -> String {
        let millis = message.timestamp
        let timestamp = Date(timeIntervalSince1970: TimeInterval(millis) / 1000)
        
        let calendar = Calendar.current
        let now = Date()
        
        let diff = calendar.dateComponents([.day, .hour, .minute], from: timestamp, to: now)
        
        if let days = diff.day,
           let hours = diff.hour,
           let minutes = diff.minute {
            if days > 1 {
                return "Mensagem recebida \(timestamp.formatted(date: .abbreviated, time: .shortened))"
            } else if days == 1 {
                return "Mensagem recebida \(days)d \(hours)h atrás"
            } else {
                return "Mensagem recebida \(hours)h \(minutes)m atrás"
            }
        }
        
        return "Mensagem recebida \(timestamp.formatted(date: .abbreviated, time: .shortened))"
    }
}

#Preview {
    HomeDashboardView {
        
    } selectMessages: {
        
    }
}
