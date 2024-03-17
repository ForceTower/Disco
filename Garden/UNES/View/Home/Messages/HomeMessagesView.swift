//
//  HomeMessagesView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 16/03/24.
//

import SwiftUI
import Club

struct HomeMessagesView: View {
    @State var path: NavigationPath = .init()
    @StateObject var vm: HomeMessagesViewModel = .init(messagesUseCase: AppDIContainer.shared.resolve())
    
    var body: some View {
        NavigationStack(path: $path) {
            ScrollView {
                LazyVStack(content: {
                    ForEach(vm.messages, id: \.id) { element in
                        MessageItemView(message: element)
                    }
                })
            }
            .navigationTitle("Mensagens")
        }
    }
}

struct MessageItemView: View {
    let message: Message
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(disciplineName())
                .font(.body)
                .fontWeight(.regular)
                .foregroundStyle(.blue)
                .multilineTextAlignment(.leading)
                .frame(alignment: .leading)
            
            if let name = senderName() {
                Text(name)
                    .font(.footnote)
                    .fontWeight(.regular)
                    .foregroundStyle(.gray)
                    .multilineTextAlignment(.leading)
                    .padding(.top, 4)
                    .frame(alignment: .leading)
            }
            
            Text(message.content)
                .font(.body)
                .fontWeight(.light)
                .multilineTextAlignment(.leading)
                .padding(.top, 8)
            
            HStack {
                Spacer()
                
                Text(messageSent())
                    .font(.caption)
                    .fontWeight(.regular)
                    .foregroundStyle(.gray)
                    .padding(.top, 8)
            }
        }
        .padding()
        .background(.background)
        .clipShape(.rect(cornerRadius: 8))
        .shadow(color: .gray.opacity(0.7), radius: 1, x: 0.2, y: 0.6)
        .padding(.horizontal)
        .padding(.vertical, 4)
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
    HomeMessagesView()
}

#Preview {
    MessageItemView(message: .init(id: 33, content: "Uma grande mensagem deixada pelos antigos professores. Dizem que esta mensagem atravessa o tempo como nenhuma outra jamais atravessou. Uma beleza da modernidade.", platformId: 54, timestamp: Int64(Date().timeIntervalSince1970 * 1000), senderProfile: 3, senderName: "Marina dos Campos Nevados", notified: 1, discipline: "Mágica e suas Poções", uuid: "fddfdfd", codeDiscipline: "EXA110", html: 0, dateString: "Grande dia", processingTime: 0, hashMessage: 12, attachmentName: nil, attachmentLink: nil))
}
