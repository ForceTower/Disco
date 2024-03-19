//
//  FinalCountdownView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 18/03/24.
//

import SwiftUI



struct FinalCountdownView: View {
    @StateObject private var vm: FinalCountdownViewModel = .init()
    @State private var showAddValueSheet = false
    @State private var editObject: MechValue? = nil
    
    var body: some View {
        VStack {
            List {
                if !vm.values.isEmpty {
                    Section("Notas inseridas") {
                        ForEach(vm.values) { item in
                            
                            HStack(alignment: .firstTextBaseline, spacing: 0) {
                                Text("Nota:")
                                
                                if let value = item.grade {
                                    Text("\(String(format: "%.1f", value))")
                                        .fontWeight(.medium)
                                        .padding(.leading, 4)
                                } else {
                                    Text("?")
                                        .fontWeight(.medium)
                                        .padding(.leading, 4)
                                }
                                
                                Text("Peso:")
                                    .padding(.leading)
                                
                                Text("\(String(format: "%.1f", item.weight))")
                                    .fontWeight(.medium)
                                    .padding(.leading, 4)
                                
                                Spacer()
                                Button {
                                    editObject = item
                                    showAddValueSheet = true
                                } label: {
                                    Image(systemName: "pencil")
                                }
                            }
                        }
                        .onDelete(perform: deleteItems)
                    }
                    
                    if let result = result() {
                        Text(result)
                    }
                } else {
                    Text("A contagem começa quando você insere suas notas, os curingas e descobre o que te espera")
                        .multilineTextAlignment(.center)
                        .padding()
                }
            }
        }
        .navigationTitle("Final Countdown")
        .toolbar {
            ToolbarItem {
                Button(action: openAddSheet) {
                    Label("Adicionar nota", systemImage: "plus")
                }
            }
        }
        .sheet(isPresented: $showAddValueSheet, onDismiss: {
            editObject = nil
        }) {
            AddCountdownGradeView(editObject: editObject) { value in
                showAddValueSheet = false
                if let edit = editObject {
                    updateItem(edit, value)
                } else {
                    addItem(value)
                }
                editObject = nil
            }
            .presentationDetents([.height(350)])
        }
    }
    
    private func openAddSheet() {
        showAddValueSheet = true
    }
    
    private func addItem(_ item: MechValue) {
        withAnimation {
            vm.addValue(item)
        }
    }
    
    private func deleteItems(offsets: IndexSet) {
        withAnimation {
            vm.deleteValue(offsets)
        }
    }
    
    private func updateItem(_ old: MechValue, _ next: MechValue) {
        withAnimation {
            vm.updateItem(old, next)
        }
    }
    
    private func result() -> String? {
        guard let result = vm.result else { return nil }
        
        let wildcard = result.wildcard
        let finalGrade = result.finalGrade
        let mean = result.mean
        
        if result.lost {
            return "Você perdeu! Volte semestre que vem."
        }
        if finalGrade == nil && wildcard == nil {
            return "Sua média é \(String(format: "%.1f", mean))"
        }
        if let finalGrade = finalGrade, wildcard == nil {
            return String(format: "Sua média é %1$.1f. Você precisa de %2$.1f na final para passar", mean, finalGrade)
        }
        if let wildcard = wildcard, finalGrade == nil {
            if wildcard <= 0 {
                return "Você pode zerar o curinga e ainda vai passar direto"
            } else {
                return String(format: "Você precisa tirar %1$.1f em cada nota curinga para passar", wildcard)
            }
        }
        if let wildcard = wildcard, let finalGrade = finalGrade {
            return String(format: "Se você tirar %1$.1f nos curingas vai precisar de %2$.1f na final para passar", wildcard, finalGrade)
        }
        return nil
    }
}

struct AddCountdownGradeView: View {
    let editObject: MechValue?
    let addItem: (MechValue) -> Void
    @State private var addingGrade = ""
    @State private var addingWeight = "10"
    @State private var addingIsWildcard = false
    @State private var stringsMustBeNumbersAlert = false
    
    var body: some View {
        VStack(alignment: .leading) {
            Form {
                Text("Adicionar nova nota")
                VStack(alignment: .leading, spacing: 0) {
                    Toggle("Nota Curinga", isOn: $addingIsWildcard)
                    Text("O UNES irá calcular a nota ideal para a avaliação para que você passe direto")
                        .multilineTextAlignment(.leading)
                        .font(.caption)
                        .padding(.top, 8)
                }
                if !addingIsWildcard {
                    HStack {
                        Text("Nota:")
                        TextField("", text: $addingGrade)
                            .keyboardType(.decimalPad)
                            .submitLabel(.next)
                    }
                }
                HStack {
                    Text("Peso:")
                    TextField("", text: $addingWeight)
                        .keyboardType(.decimalPad)
                        .submitLabel(.next)
                }
                
                Button("Adicionar") {
                    guard let weight = Double(addingWeight.replacingOccurrences(of: ",", with: ".").trimmingCharacters(in: .whitespacesAndNewlines)) else {
                        stringsMustBeNumbersAlert = true
                        return
                    }
                    var grade: Double? = nil
                    if !addingIsWildcard {
                        guard let parsed = Double(addingGrade.replacingOccurrences(of: ",", with: ".").trimmingCharacters(in: .whitespacesAndNewlines)) else {
                            stringsMustBeNumbersAlert = true
                            return
                        }
                        grade = parsed
                    }
                    
                    addItem(.init(weight: weight, grade: grade))
                    addingIsWildcard = false
                    addingGrade = ""
                }
            }
        }
        .onAppear {
            if let obj = editObject {
                if let grd = obj.grade {
                    addingGrade = String(format: "%.1f", grd)
                } else {
                    addingIsWildcard = true
                }
                addingWeight = String(format: "%.1f", obj.weight)
            }
        }
        .alert("Valores incorretos", isPresented: $stringsMustBeNumbersAlert, actions: {
            Button("Ok", action: {
                stringsMustBeNumbersAlert = false
            })
        }, message: {
            Text("Algum dos valores inseridos não é um numero válido.")
        })
    }
}

#Preview {
    @State var obj: MechValue? = nil
    return AddCountdownGradeView(editObject: obj) { item in
        
    }
}

#Preview {
    NavigationStack {
        FinalCountdownView()
    }
}
