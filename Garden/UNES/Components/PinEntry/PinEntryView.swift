//
//  PinEntryView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 20/03/24.
//

import SwiftUI

struct PinEntryView: View {
    @Binding var pinCode: String
    
    let pinLimit: Int = 10
    var isError: Bool = false
    var canEdit: Bool = true
    
    private var pins: [String] {
        return pinCode.map { String($0) }
    }
    
    var body: some View {
        ZStack {
            VStack {
                PinCodeTextField(limit: pinLimit, canEdit: canEdit, text: $pinCode)
                    .border(Color.black, width: 1)
                    .frame(height: 60)
                    .padding()
            }
            .opacity(0)
            
            VStack {
                HStack(spacing: 10) {
                    ForEach(0 ..< 10) { item in
                        if item < pinCode.count {
                            Text(pins[item])
                                .font(.title)
                                .bold()
                                .foregroundColor(isError ? .red : .primary)
                            
                        } else {
                            Circle()
                                .stroke(Color.secondary, lineWidth: 4)
                                .frame(width: 16, height: 16)
                        }
                    }
                    .frame(width: 24, height: 32)
                }
            }
        }
    }
}

struct PinCodeTextField: UIViewRepresentable {
    
    class Coordinator: NSObject, UITextFieldDelegate {
        
        var limit: Int
        var canEdit: Bool
        @Binding var text: String
        
        init(limit: Int, canEdit: Bool, text: Binding<String>) {
            self.limit = limit
            self.canEdit = canEdit
            self._text = text
        }
        
        func textFieldDidChangeSelection(_ textField: UITextField) {
            DispatchQueue.main.async {
                self.text = textField.text ?? ""
            }
        }
        
        func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
            if !canEdit {
                return false
            }
            
            let currentText = textField.text ?? ""
            
            guard let stringRange = Range(range, in: currentText) else { return false }
            
            let updatedText = currentText.replacingCharacters(in: stringRange, with: string)
            
            return updatedText.count <= limit
        }
    }
    
    var limit: Int
    var canEdit: Bool
    @Binding var text: String
    
    func makeUIView(context: UIViewRepresentableContext<PinCodeTextField>) -> UITextField {
        let textField = UITextField(frame: .zero)
        textField.delegate = context.coordinator
        textField.textAlignment = .center
        textField.autocapitalizationType = .allCharacters
        return textField
    }
    
    func makeCoordinator() -> PinCodeTextField.Coordinator {
        return Coordinator(limit: limit, canEdit: canEdit, text: $text)
    }
    
    func updateUIView(_ uiView: UITextField, context: UIViewRepresentableContext<PinCodeTextField>) {
        uiView.text = text
        context.coordinator.canEdit = canEdit
        uiView.becomeFirstResponder()
    }
}

struct StatefulPreviewWrapper<Value, Content: View>: View {
    var content: (Binding<Value>) -> Content
    @State private var value: Value
    
    init(_ value: Value, content: @escaping (Binding<Value>) -> Content) {
        self._value = State(wrappedValue: value)
        self.content = content
    }
    
    var body: some View {
        content($value)
    }
}

#Preview {
    @State var pin: String = ""
    return PinEntryView(pinCode: $pin)
}

