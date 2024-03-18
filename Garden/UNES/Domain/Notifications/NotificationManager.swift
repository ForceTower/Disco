//
//  NotificationManager.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 18/03/24.
//

import UserNotifications
import FirebaseCrashlytics
import Club

class NotificationManager {
    static let shared = NotificationManager()
    
    func requestPermission() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound, .provisional]) { success, err in
            if let err = err {
                Crashlytics.crashlytics().log("Failed to request notification authorization")
                Crashlytics.crashlytics().record(error: err)
            }
        }
    }
    
    func createNotification(forMessage message: Message) async throws {
        var title = "Nova mensagem!"
        if message.senderProfile == 3 {
            title = "UEFS"
        } else if let discipline = message.discipline {
            title = discipline
        } else if let sender = message.senderName {
            title = sender
        }
        
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = message.content
        content.sound = .default
        
        let request = UNNotificationRequest(
            identifier: "Message_\(message.id)",
            content: content,
            trigger: nil
        )
        
        try await UNUserNotificationCenter.current().add(request)
    }
    
    func createNotification(forGrade grade: GradeData) async throws {
        if grade.ref.notified == 0 { return }
        var title = "Notas!"
        var body = "Houveram mudanças nas notas"
        
        switch(grade.ref.notified) {
        case 1:
            title = "Avaliação criada"
            body = "A \(grade.ref.name) da disciplina \(grade.discipline.name) foi criada mas não há notas associadas"
        case 2:
            title = "Data de avaliação modificada"
            body = "A data da avaliação \(grade.ref.name) da disciplina \(grade.discipline.name) foi alterada"
        case 3:
            title = "Nota postada"
            body = "A nota da \(grade.ref.name) da disciplina \(grade.discipline.name) está disponível"
        case 4:
            title = "Nota alterada"
            body = "A nota da \(grade.ref.name) da disciplina \(grade.discipline.name) foi alterada"
        default:
            print("nothing")
            return
        }
        
        
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = body
        content.sound = .default
        
        let request = UNNotificationRequest(
            identifier: "Grade_\(grade.ref.id)",
            content: content,
            trigger: nil
        )
        
        try await UNUserNotificationCenter.current().add(request)
    }
}

