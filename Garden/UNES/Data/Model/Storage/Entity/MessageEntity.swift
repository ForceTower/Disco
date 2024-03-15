//
//  MessageEntity.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 14/03/24.
//

import SwiftData
import Foundation

@Model
class MessageEntity {
    @Attribute(.unique)
    var platformId: UInt64
    var content: String
    var timestamp: Date
    var hashMessage: Int64
    var senderProfile: Int16
    var html: Bool
    var notified: Bool
    var codeDiscipline: String?
    var attachmentLink: String?
    var attachmentName: String?
    var discipline: String?
    var processingTime: Date?
    var senderName: String?
    
    init(platformId: UInt64, 
         content: String,
         timestamp: Date,
         hashMessage: Int64,
         senderProfile: Int16,
         html: Bool = false,
         notified: Bool = false,
         codeDiscipline: String?,
         attachmentLink: String?,
         attachmentName: String?,
         discipline: String?,
         processingTime: Date?,
         senderName: String?
    ) {
        self.platformId = platformId
        self.content = content
        self.timestamp = timestamp
        self.codeDiscipline = codeDiscipline
        self.attachmentLink = attachmentLink
        self.attachmentName = attachmentName
        self.discipline = discipline
        self.hashMessage = hashMessage
        self.html = html
        self.notified = notified
        self.processingTime = processingTime
        self.senderName = senderName
        self.senderProfile = senderProfile
    }
}
