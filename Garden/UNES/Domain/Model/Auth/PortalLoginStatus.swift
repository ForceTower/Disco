//
//  PortalLoginStatus.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 10/03/24.
//

import Club

enum PortalLoginStatus {
    case handshake
    case fetchedUser(person: SingerPerson)
    case fetchedMessages
    case fetchedSemesterInfo
    case fetchedGrades
}
