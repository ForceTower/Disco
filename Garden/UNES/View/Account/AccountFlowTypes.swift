//
//  AccountData.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 19/03/24.
//

import Foundation

struct RootAccFlow : Hashable {}
struct HandshakeAccFlow : Hashable {}
struct EmailAccFlow : Hashable {}
struct EmailConfirmationAccFlow : Hashable  {
    let security: String
}

struct PasskeyCreateAccFlow : Hashable {}
struct PasskeyListAccFlow : Hashable {}
