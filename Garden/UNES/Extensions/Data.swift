//
//  Data.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 20/03/24.
//

import Foundation

extension Data {
    func base64URLEncode() -> String {
        let base64 = self.base64EncodedString()
        let base64URL = base64
            .replacingOccurrences(of: "+", with: "-")
            .replacingOccurrences(of: "/", with: "_")
            .replacingOccurrences(of: "=", with: "")
        return base64URL
    }
    
    func base64URLEncodePadded() -> String {
        let base64 = self.base64EncodedString()
        let base64URL = base64
            .replacingOccurrences(of: "+", with: "-")
            .replacingOccurrences(of: "/", with: "_")
        return base64URL
    }
}
