//
//  AccountData.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 19/03/24.
//

import Foundation

enum AccountFlow: Hashable {
    case root, handshake, email
    case confirmation(security: String)
    case passkeyCreate, passkeyList
}
