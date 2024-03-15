//
//  Assertion.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 10/03/24.
//

import Foundation

struct SimplifiedPublicKey: Codable {
    let challenge: String
    let timeout: Int
    let rpId: String
    let userVerification: String
}

struct PasskeyAssert: Codable {
    let publicKey: SimplifiedPublicKey
}

struct AssertionStartData: Codable {
    let flowId: String
    let challenge: PasskeyAssert
}
