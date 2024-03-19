//
//  SettingsView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 19/03/24.
//

import SwiftUI

enum FrequencyOption: String, Codable, CaseIterable {
    case minutes15, minutes30, hour1, hour2, hour4, disabled
    
    func strName() -> String {
        return switch self {
        case .minutes15: "15 minutos"
        case .minutes30: "30 minutos"
        case .hour1: "1 hora"
        case .hour2: "2 horas"
        case .hour4: "4 horas"
        case .disabled: "Desativado"
        }
    }
}

enum DisciplineSyncFrequency: String, Codable, CaseIterable {
    case times2, times4, all, manual
    
    func strName() -> String {
        return switch self {
        case .times2: "2 vezes por dia"
        case .times4: "4 vezes por dia"
        case .all: "Todas as vezes"
        case .manual: "Manual"
        }
    }
}

enum SubtitleOption: String, Codable, CaseIterable {
    case score, semester, course, university, none
    
    func strName() -> String {
        return switch self {
        case .score: "Score"
        case .semester: "Semestre"
        case .course: "Curso"
        case .university: "Universidade"
        case .none: "Nada"
        }
    }
}

enum SpoilerOption: String, Codable, CaseIterable {
    case none, moderate, full
    
    func strName() -> String {
        return switch self {
        case .none: "Nenhum"
        case .moderate: "Moderado"
        case .full: "Completo"
        }
    }
}

struct SettingsView: View {
    @AppStorage("settings_sync_frequency") private var selectedFrequency: FrequencyOption = .minutes15
    @AppStorage("settings_sync_discipline") private var selectedDiscipline: DisciplineSyncFrequency = .manual
    @AppStorage("settings_exhibition_subtitle") private var selectSubtitle: SubtitleOption = .course
    @AppStorage("settings_exhibition_grade_spoiler") private var selectedSpoiler: SpoilerOption = .none
    @AppStorage("settings_exhibition_schedule_shrink") private var shrinkSchedule: Bool = true
    
    @StateObject private var vm: SettingsViewModel = .init()
    
    private var frequencies: [FrequencyOption] = [.disabled, .minutes15, .minutes30, .hour1, .hour2, .hour4]
    private var disciplineData: [DisciplineSyncFrequency] = [.times2, .times4, .all, .manual]
    private var subtitles: [SubtitleOption] = [.score, .semester, .course, .university, .none]
    private var gradeSpoilers: [SpoilerOption] = [.none, .moderate, .full]
    
    var body: some View {
        List {
            Section("Sincronização") {
                Picker("Frequência mínima", selection: $selectedFrequency) {
                    ForEach(frequencies, id: \.self) { frequency in
                        Text(frequency.strName())
                    }
                }
                
                Picker(selection: $selectedDiscipline) {
                    ForEach(disciplineData, id: \.self) { item in
                        Text(item.strName())
                    }
                } label: {
                    Text("Disciplinas")
                }
            }
            
            Section("Exibição") {
                Picker(selection: $selectSubtitle) {
                    ForEach(subtitles, id: \.self) { item in
                        Text(item.strName())
                    }
                } label: {
                    Text("Subtítulo")
                    if selectSubtitle == .score {
                        Text("O score é o mais ofensivo de todos")
                            .font(.caption)
                    } else if selectSubtitle == .semester {
                        Text("O semestre pode ser ofensivo")
                            .font(.caption)
                    }
                }
                
                Picker(selection: $selectedSpoiler) {
                    ForEach(gradeSpoilers, id: \.self) { item in
                        Text(item.strName())
                    }
                } label: {
                    Text("Spoiler das Notas")
                    Text("Cuidado, eles podem ser crueis")
                        .font(.caption)
                }
                
                Toggle(isOn: $shrinkSchedule, label: {
                    Text("Encolher horário")
                    if shrinkSchedule {
                        Text("Juntando horários proximos")
                            .font(.caption)
                    } else {
                        Text("Exatamente como o portal")
                            .font(.caption)
                    }
                })
            }
        }
        .navigationTitle("Configurações")
        .onChange(of: selectedFrequency) { newValue in
            vm.changeSetting(syncFrequency: newValue)
        }
    }
}

#Preview {
    NavigationStack {
        SettingsView()
    }
}
