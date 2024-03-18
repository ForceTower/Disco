//
//  DisciplineDetailsView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 17/03/24.
//

import Club
import SwiftUI

struct DisciplineDetailsView: View {
    let groupId: Int64
    @State var selection: Int = 1
    @Binding var path: NavigationPath
    @Environment(\.openURL) private var openURL
    @StateObject private var vm: DisciplineDetailsViewModel = .init(classData: AppDIContainer.shared.resolve())
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            if let data = vm.data {
                DisciplineHeaderView(data: data, selection: $selection)
            } else {
                ProgressView()
            }
            TabView(selection: $selection, content:  {
                DisciplineOverviewView(data: vm.data)
                    .tag(1)
                DisciplineItemsView(items: vm.items)
                    .tag(2)
                DisciplineMaterialsView(items: vm.materials) { material in
                    if let url = URL(string: material.link) {
                        openURL(url)
                    }
                }
                .tag(3)
                DisciplineAbsencesView(items: vm.absences)
                    .tag(4)
            })
            .tabViewStyle(.page(indexDisplayMode: .never))
            .padding(.top, 4)
        }
        .onAppear(perform: {
            vm.fetchDataFor(groupId: groupId)
            vm.loadDataFor(groupId: groupId)
        })
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct DisciplineHeaderView: View {
    let data: ClassGroupData
    @Binding var selection: Int
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(WordUtils.shared.toTitleCase(str: data.discipline.name))
                .font(.title2)
                .multilineTextAlignment(.leading)
                .padding(.horizontal)
                .padding(.top, 16)
            
            if let department = data.discipline.department {
                Text(department)
                    .font(.callout)
                    .fontWeight(.light)
                    .foregroundStyle(.gray)
                    .multilineTextAlignment(.leading)
                    .padding(.horizontal)
                    .padding(.top, 2)
            }
            
            Text(missedClasses())
                .multilineTextAlignment(.leading)
                .padding(.horizontal)
                .padding(.top, 8)
            
            HStack {
                HStack {
                    Text("\(data.discipline.credits)h")
                    Image(systemName: "clock")
                }
                .padding(.trailing, 8)
                
                Divider()
                    .foregroundStyle(.gray)
                    .frame(height: 48)
                VStack {
                    HStack {
                        Text("\(data.clazz.missedClasses)")
                        Image(systemName: "backpack")
                    }
                    Text("faltas")
                }
                .padding(.leading, 8)
            }
            .padding(.horizontal)
            .padding(.vertical, 8)
            
            ScrollView([.horizontal]) {
                HStack {
                    Text("Visão Geral")
                        .font(.callout)
                        .foregroundStyle(selection == 1 ? .blue : .gray)
                        .onTapGesture { selection = 1 }
                    
                    Text("Aulas")
                        .font(.callout)
                        .foregroundStyle(selection == 2 ? .blue : .gray)
                        .onTapGesture { selection = 2 }
                    
                    Text("Anexos")
                        .font(.callout)
                        .foregroundStyle(selection == 3 ? .blue : .gray)
                        .onTapGesture { selection = 3 }
                    
                    Text("Faltas")
                        .font(.callout)
                        .foregroundStyle(selection == 4 ? .blue : .gray)
                        .onTapGesture { selection = 4 }
                }
                .padding(.horizontal)
            }
            .padding(.top)
            .padding(.bottom)
        }
        .background(.background)
        .clipShape(.rect(cornerRadius: 8))
        .shadow(radius: 2)
        .padding(.horizontal)
        .padding(.top)
    }
    
    func missedClasses() -> String {
        let hours = Int(data.discipline.credits)
        let left = (hours / 4) - Int(data.clazz.missedClasses)
        
        if left == 0 {
            return "Você não pode mais faltar"
        } else if left > 0 {
            return "Ainda faltam \(left) faltas"
        } else {
            return "Você perdeu por faltas ☠. Tente negociar com o professor 🫠"
        }
    }
}

struct DisciplineOverviewView: View {
    let data: ClassGroupData?
    
    var body: some View {
        if let data = data {
            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    if let teacher = data.group.teacher {
                        HStack {
                            VStack(alignment: .leading) {
                                Text("Professor (a)")
                                    .font(.callout)
                                
                                Text(teacher)
                                    .font(.callout)
                                    .fontWeight(.medium)
                                    .foregroundStyle(.foreground.opacity(0.6))
                            }
                            Spacer()
                            Image(systemName: "chevron.right")
                                .foregroundStyle(.blue)
                        }
                        .background()
                        .padding(.horizontal)
                    }
                    
                    if let program = data.discipline.resume {
                        VStack(alignment: .leading, spacing: 0) {
                            Text("Ementa")
                                .font(.callout)
                                .fontWeight(.medium)
                            
                            Text(program)
                                .font(.callout)
                                .padding(.top, 8)
                                .foregroundStyle(.foreground.opacity(0.8))
                        }
                        .padding()
                        .background(.green.opacity(0.4))
                        .clipShape(.rect(cornerRadius: 10))
                        .padding()
                    }
                }
                .padding(.top)
            }
        } else {
            ProgressView()
        }
    }
}

struct DisciplineItemsView: View {
    let items: [ClassItem]
    
    var body: some View {
        ScrollView {
            VStack {
                if items.isEmpty {
                    VStack {
                        Image(uiImage: UIImage(resource: .schoolItems))
                            .resizable()
                            .scaledToFit()
                            .frame(height: 120)
                        Text("As aulas desta disciplina ainda não foram registradas")
                            .multilineTextAlignment(.center)
                    }
                    .padding()
                } else {
                    LazyVStack(alignment: .leading, content: {
                        ForEach(items, id: \.id) { item in
                            ClassItemView(item: item)
                        }
                    })
                }
            }
            .padding(.top)
        }
    }
}

struct DisciplineMaterialsView: View {
    let items: [ClassMaterial]
    let onMaterialSelected: (ClassMaterial) -> Void
    
    var body: some View {
        ScrollView {
            VStack {
                if items.isEmpty {
                    VStack {
                        Image(uiImage: UIImage(resource: .schoolItems))
                            .resizable()
                            .scaledToFit()
                            .frame(height: 120)
                        Text("O UNES não encontrou nenhum anexo nesta turma")
                            .multilineTextAlignment(.center)
                    }
                    .padding()
                } else {
                    LazyVStack(alignment: .leading, content: {
                        ForEach(items, id: \.id) { item in
                            ClassMaterialView(item: item)
                                .onTapGesture {
                                    onMaterialSelected(item)
                                }
                        }
                    })
                }
            }
            .padding(.top)
        }
    }
}

struct DisciplineAbsencesView: View {
    let items: [ClassAbsence]
    
    var body: some View {
        ScrollView {
            VStack {
                if items.isEmpty {
                    VStack {
                        Image(uiImage: UIImage(resource: .relaxedAtHome))
                            .resizable()
                            .scaledToFit()
                            .frame(height: 120)
                        Text("O UNES não encontrou nenhuma aula marcada com falta nesta turma")
                            .multilineTextAlignment(.center)
                    }
                    .padding()
                } else {
                    LazyVStack(alignment: .leading, content: {
                        ForEach(items, id: \.id) { item in
                            ClassAbsenceItemView(item: item)
                        }
                    })
                }
            }
            .padding(.top)
        }
    }
}

struct ClassItemView: View {
    let item: ClassItem
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(item.subject ?? "????")
                .strikethrough(item.situation?.lowercased() == "realizada")
                .font(.callout)
            
            HStack(alignment: .center, spacing: 8) {
                Image(systemName: "calendar")
                    .resizable()
                    .resizable()
                    .scaledToFit()
                    .frame(width: 16, height: 16)
                
                Text(formatDate() ?? "????")
                    .font(.footnote)
                    .foregroundStyle(.foreground.opacity(0.6))
                
                Image(systemName: "paperclip")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 16, height: 16)
                    .foregroundStyle(.yellow)
                    .padding(.leading, 8)
                
                Text("\(item.numberOfMaterials)")
                    .font(.footnote)
                
                Spacer()
            }
            .padding(.top, 4)
        }
        .padding(.horizontal)
        .padding(.vertical, 12)
        .background()
        .clipShape(.rect(cornerRadius: 8))
        .shadow(radius: 0.5)
        .padding(.horizontal)
    }
    
    func formatDate() -> String? {
        guard let date = item.date else {
            return nil
        }
        if let parsed = try? Date(date, strategy: .iso8601) {
            return parsed.formatted(date: .numeric, time: .omitted)
        } else {
            return nil
        }
    }
}

struct ClassMaterialView: View {
    let item: ClassMaterial
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(item.name)
                .lineLimit(2)
                .multilineTextAlignment(.leading)
            
            if let url = URL(string: item.link) {
                Link(destination: url) {
                    Text(item.link)
                        .multilineTextAlignment(.leading)
                        .lineLimit(2)
                        .font(.footnote)
                        .foregroundStyle(.blue)
                        .underline(true, color: .blue)
                }
            } else {
                Text("Link quebrado: \(item.link)")
                    .font(.footnote)
                    .foregroundStyle(.red)
            }
            HStack {
                Spacer()
            }
        }
        .padding()
        .background()
    }
}

struct ClassAbsenceItemView: View {
    let item: ClassAbsence
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("Aula \(item.sequence): \(item.date)")
                .font(.callout)
                .foregroundStyle(.blue)
            
            Text(item.description_)
                .font(.callout)
                .padding(.top, 2)
            HStack {
                Spacer()
            }
        }
        .padding()
        .background()
    }
}

#Preview {
    @State var path: NavigationPath = .init()
    return NavigationStack(path: $path) {
        DisciplineDetailsView(groupId: 5, path: $path)
    }
}
