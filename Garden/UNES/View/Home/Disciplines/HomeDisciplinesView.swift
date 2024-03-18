//
//  HomeDisciplinesView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 17/03/24.
//

import SwiftUI
import Club

struct HomeDisciplinesView: View {
    @State var path: NavigationPath = .init()
    @StateObject private var vm: HomeDisciplinesViewModel = .init(disciplinesUseCase: AppDIContainer.shared.resolve(), syncUseCase: AppDIContainer.shared.resolve())
    
    @State var showGroupSelector = false
    @State var selectorGroups: [ClassGroup] = []
    
    var body: some View {
        NavigationStack(path: $path) {
            ScrollView {
                LazyVStack(content: {
                    ForEach(vm.semesters, id: \.semester.id) { element in
                        HomeDisciplineSemesterItemView(data: element) { semester in
                            vm.fetchDataFor(semester: semester)
                        } onClassSelected: { data in
                            if data.groups.count == 1 {
                                path.append(data.groups[0])
                            } else {
                                showGroupSelector(data.groups)
                            }
                        }
                    }
                })
            }
            .refreshable {
                await Task {
                    await vm.syncData()
                }.value
            }
            .navigationDestination(for: ClassGroup.self) { item in
                DisciplineDetailsView(groupId: item.id, path: $path)
            }
            .navigationTitle("Disciplinas")
            .confirmationDialog("Selecione uma turma", isPresented: $showGroupSelector, titleVisibility: .visible) {
                ForEach(selectorGroups, id: \.id) { group in
                    Button(action: {
                        selectGroup(group)
                    }, label: {
                        Text(group.group)
                    })
                }
            } message: {
                Text("Sobre qual turma você deseja ver mais detalhes?")
            }

        }
    }
    
    func showGroupSelector(_ groups: [ClassGroup]) {
        showGroupSelector = true
        selectorGroups = groups
    }
    
    func selectGroup(_ group: ClassGroup) {
        showGroupSelector = false
        selectorGroups = []
        path.append(group)
    }
}

struct HomeDisciplineSemesterItemView: View {
    let data: SemesterClassData
    let fetchSemesterData: (Semester) -> Void
    let onClassSelected: (ClassData) -> Void
    
    var body: some View {
        VStack {
            HomeDisciplineSemesterNameView(semester: data.semester.name)
            
            if data.classes.isEmpty {
                VStack {
                    Text("As disciplinas do semestre \(data.semester.name) ainda não foram baixadas.\nIsso tem impacto direto no score calculado.")
                        .multilineTextAlignment(.center)
                    Button(action: {
                        fetchSemesterData(data.semester)
                    }, label: {
                        Text("Baixar")
                    })
                }
                .padding(.bottom, 32)
                .padding(.top, 8)
            } else {
                LazyVStack(spacing: 0) {
                    ForEach(data.classes, id: \.clazz.id) { item in
                        HomeDisciplineItemView(item: item)
                            .onTapGesture {
                                onClassSelected(item)
                            }
                            .padding(.bottom, 32)
                    }
                }
            }
        }
        .padding(.top, 8)
    }
}

struct HomeDisciplineSemesterNameView: View {
    let semester: String
    
    var body: some View {
        Text(semester)
            .font(.title3)
            .fontWeight(.medium)
            .foregroundStyle(.blue)
            .multilineTextAlignment(.leading)
            .frame(alignment: .leading)
    }
}

struct HomeDisciplineItemView: View {
    let item: ClassData
    
    let evaluations = ["AV1 - Nota de Desempenho do Aluno", "AV2 - Nota de Relatório Parcial"]
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(WordUtils.shared.toTitleCase(str: item.discipline.name))
                .font(.title3)
                .fontWeight(.medium)
            
            HStack(alignment: .firstTextBaseline,spacing: 0) {
                Text(item.discipline.code)
                    .font(.footnote)
                    .fontWeight(.regular)
                    .foregroundStyle(.blue)
                
                Spacer()
                
                if let department = item.discipline.department {
                    Text(department)
                        .font(.footnote)
                        .fontWeight(.regular)
                        .foregroundStyle(.gray)
                }
            }
            .padding(.top, 4)
            
            LazyVStack(spacing: 0) {
                ForEach(item.grades, id: \.original.id) { grade in
                    VStack(alignment: .leading, spacing: 0) {
                        HStack(spacing: 0) {
                            VStack(alignment: .leading, spacing: 0) {
                                Text(grade.name)
                                    .font(.body)
                                    .fontWeight(.medium)
                                
                                if let date = grade.dateSeconds?.int64Value {
                                    Text(Date(timeIntervalSince1970: TimeInterval(date)).formatted(date: .numeric, time: .omitted))
                                        .font(.footnote)
                                        .fontWeight(.regular)
                                        .foregroundStyle(.gray)
                                        .padding(.top, 2)
                                } else {
                                    Text("Data não divulgada")
                                        .font(.footnote)
                                        .fontWeight(.regular)
                                        .foregroundStyle(.gray)
                                        .padding(.top, 2)
                                }
                            }
                            Spacer()
                            Text(grade.grade ?? "-")
                                .font(.body)
                                .fontWeight(.medium)
                                .foregroundStyle(.blue)
                            
                        }
                    }
                    .padding(.vertical, 4)
                }
            }
            .padding(.vertical, 8)
            
            if let partialScore = item.clazz.partialScore?.doubleValue {
                HStack(spacing: 0) {
                    Text("Média Parcial")
                        .fontWeight(.regular)
                    
                    Spacer()
                    
                    Text(String(format: "%.1f", partialScore))
                        .fontWeight(.bold)
                        .foregroundStyle(.cyan)
                }
            }
            
            if let finalScore = item.clazz.finalScore?.doubleValue {
                HStack(spacing: 0) {
                    Text("Média Final")
                        .fontWeight(.regular)
                    
                    Spacer()
                    
                    Text(String(format: "%.1f", finalScore))
                        .fontWeight(.bold)
                        .foregroundStyle(.cyan)
                }
            }
        }
        .padding(.horizontal)
        .background(.background)
    }
}

#Preview {
    HomeDisciplinesView()
}

//#Preview {
//    HomeDisciplineItemView()
//}
