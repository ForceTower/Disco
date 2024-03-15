//
//  PortalLoginStatus.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 10/03/24.
//

import Arcadia

enum PortalLoginStatus {
    case handshake
    case fetchedUser(person: Person)
    case fetchedMessages
    case fetchedSemesterInfo
    case fetchedGrades
}
