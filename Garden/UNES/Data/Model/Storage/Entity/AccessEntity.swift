//
//  MessageEntity.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 10/03/24.
//

import SwiftData

@Model
class AccessEntity {
    @Attribute(.unique) var username: String
    var password: String
    var valid: Bool
    
    init(username: String, password: String, valid: Bool = true) {
        self.username = username
        self.password = password
        self.valid = valid
    }
}
