//
//  HomeScheduleView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 16/03/24.
//

import SwiftUI
import Club

struct HomeScheduleView: View {
    @State var path: NavigationPath = .init()
    
    @StateObject var vm: HomeScheduleViewModel = .init(scheduleUseCase: AppDIContainer.shared.resolve())
    
    var body: some View {
        NavigationStack(path: $path) {
            ScrollView([.vertical]) {
                VStack(spacing: 0) {
                    ScrollView([.horizontal]) {
                        VStack(alignment: .leading, spacing: 2) {
                            ForEach(vm.blocks, id: \.id) { row in
                                HStack(spacing: 2) {
                                    ForEach(row.items, id: \.id) { tile in
                                        ScheduleTileView(tile: tile) { code in
                                            vm.colorIndices[code]?.intValue ?? 0
                                        }
                                    }
                                }
                                .padding(.top, 1)
                            }
                            .padding(.trailing, 12)
                            .padding(.leading, 8)
                        }
                        .frame(alignment: .leading)
                    }
                    .scrollIndicators(.never)
                    
                    VStack(alignment: .leading, spacing: 4) {
                        ForEach(vm.lines, id: \.id) { line in
                            ScheduleHorizontalView(item: line)
                        }
                    }
                    .padding(.bottom)
                }
            }
            .navigationTitle("Horários")
        }
    }
}

struct ScheduleTileView: View {
    let tile: ProcessedClassLocation
    let findColorIndex: (String) -> Int
    
    var body: some View {
        switch tile {
        case is ProcessedClassLocation.EmptySpace:
            VStack {
                Spacer()
            }
            .frame(width: 56, height: 27)
        case let day as ProcessedClassLocation.DaySpace:
            VStack {
                Text(day.day)
                    .font(.caption)
            }
            .frame(width: 56, height: 27)
        case let time as ProcessedClassLocation.TimeSpace:
            VStack {
                Text(time.start)
                    .font(.caption)
                Text(time.end)
                    .font(.caption)
            }
            .frame(width: 56, height: 48)
        case let element as ProcessedClassLocation.ElementSpace:
            VStack {
                Text(element.reference.discipline.code)
                    .font(.caption)
                Text(element.reference.group.group)
                    .font(.caption2)
            }
            .frame(width: 56, height: 48)
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(lineWidth: 0.8)
                    .foregroundStyle(findColor(element.reference.discipline.code))
            )
            .background(findColor(element.reference.discipline.code).opacity(0.1))
            .clipShape(.rect(cornerRadius: 8))
        default:
            VStack {
                Text("What?")
                    .font(.caption)
            }
            .frame(width: 56, height: 48)
        }
    }
    func findColor(_ code: String) -> Color {
        let index = findColorIndex(code)
        let color = HomeScheduleViewModel.colors[index % HomeScheduleViewModel.colors.count]
        return color
    }
}

struct ScheduleHorizontalView : View {
    let item: LinedClassLocation
    
    var body: some View {
        switch item {
        case let day as LinedClassLocation.DaySpace:
            VStack {
                Text(day.day)
                    .font(.headline)
                    .foregroundStyle(.yellow)
            }
            .padding(.horizontal)
            .padding(.top)
        case let space as LinedClassLocation.ElementSpace:
            HStack(spacing: 0) {
                VStack(spacing: 0) {
                    Text(space.reference.location.startsAt)
                        .font(.caption)
                        .fontWeight(.regular)
                    
                    Rectangle()
                        .frame(width: 24, height: 1.5)
                        .padding(.vertical, 4)
                        .foregroundStyle(.blue.opacity(0.5))
                    
                    Text(space.reference.location.endsAt)
                        .font(.caption)
                        .fontWeight(.regular)
                }
                .frame(width: 48)
                
                VStack(alignment: .leading, spacing: 0) {
                    Text(WordUtils.shared.toTitleCase(str: space.reference.discipline.name))
                        .lineLimit(1)
                        .multilineTextAlignment(.leading)
                        .font(.caption)
                        .fontWeight(.regular)
                    
                    Rectangle()
                        .frame(height: 1.5)
                        .padding(.vertical, 4)
                        .foregroundStyle(.gray.opacity(0.3))
                    
                    HStack(spacing: 0) {
                        Text(space.reference.discipline.code)
                            .font(.caption)
                            .fontWeight(.regular)
                        
                        if let modulo = space.reference.location.modulo {
                            Text(WordUtils.shared.toTitleCase(str: modulo))
                                .font(.caption)
                                .fontWeight(.regular)
                                .padding(.leading)
                        }
                        
                        if let room = space.reference.location.room {
                            Text(room)
                                .font(.caption)
                                .fontWeight(.regular)
                                .padding(.leading)
                        }
                    }
                }
                .padding(.leading)
            }
            .padding(.horizontal, 8)
            .padding(.top, 1)
            .frame(height: 56, alignment: .leading)
            .background(.white)
            .clipShape(.rect(cornerRadius: 8))
            .shadow(color: .gray.opacity(0.7),radius: 1, x: 0.2, y: 0.7)
            .padding(.horizontal)
        default:
            VStack {
                Text("?????")
            }
        }
    }
}

#Preview {
    HomeScheduleView()
}
