//
//  String.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 20/03/24.
//

import Foundation

extension String {
    var fixedBase64Format: Self {
        let offset = count % 4
        guard offset != 0 else { return self }
        return padding(toLength: count + 4 - offset, withPad: "=", startingAt: 0)
    }
    
    var thingy: Self {
        self
            .replacingOccurrences(of: "-", with: "+")
            .replacingOccurrences(of: "_", with: "/")
    }
}
