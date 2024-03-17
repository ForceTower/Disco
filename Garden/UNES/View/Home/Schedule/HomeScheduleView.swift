//
//  HomeScheduleView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 16/03/24.
//

import SwiftUI
import Club

struct Line: Identifiable {
    let id = UUID()
    let items: [Tile]
}

struct Tile: Identifiable {
    let id = UUID()
    let element: ProcessedClassLocation
}

struct Horizontal: Identifiable {
    let id = UUID()
    let element: ProcessableLocation
}

enum ProcessableLocation {
    case day(DaySpace), element(ElementSpace)
}

enum ProcessedClassLocation {
    case empty
    case element(ElementSpace)
    case time(TimeSpace)
    case day(DaySpace)
}

struct ElementSpace {
    let location: String
    let group: String
}

struct TimeSpace {
    let start: String
    let end: String
    let startInt: Int
    let endInt: Int
}

struct DaySpace {
    let day: String
    let dayInt: Int
}

let l1: [Tile] = [.empty, .day(DaySpace(day: "SEG", dayInt: 1)), .day(.init(day: "TER", dayInt: 2))].map({ Tile(element: $0)})
let l2: [Tile] = [.time(.init(start: "10:30", end: "12:30", startInt: 1030, endInt: 1230)), .element(.init(location: "EXA220", group: "T01")), .element(.init(location: "TEC440", group: "P02"))].map({ Tile(element: $0)})
let l3: [Tile] = [.time(.init(start: "14:30", end: "18:30", startInt: 1430, endInt: 1830)), .empty, .element(.init(location: "FIS330", group: "T01P03"))].map({ Tile(element: $0)})

let common: [Horizontal] = [
    .init(element: .day(.init(day: "Segunda", dayInt: 1))),
    .init(element: .element(.init(location: "Cálculo II", group: "T01"))),
    .init(element: .element(.init(location: "Principios da Condução Elétrica em Silício", group: "P02"))),
    .init(element: .day(.init(day: "Terça", dayInt: 2))),
    .init(element: .element(.init(location: "Física III", group: "T01P03"))),
]

struct HomeScheduleView: View {
    @State var path: NavigationPath = .init()
    
    let elements: [Line] = [
        Line(items: l1),
        Line(items: l2),
        Line(items: l3)
    ]
    
    var body: some View {
        NavigationStack(path: $path) {
            GeometryReader(content: { geometry in
                ScrollView {
                    VStack(alignment: .leading) {
                        ForEach(elements) { row in
                            HStack {
                                ForEach(row.items) { tile in
                                    ScheduleTileView(tile: tile)
                                }
                            }.padding(.top, 1)
                        }
                        
                        ForEach(common) { item in
                            ScheduleHorizontalView(item: item, geometry: geometry)
                        }
                    }
                    .frame(width: geometry.size.width, alignment: .leading)
                }
            })
            .navigationTitle("Horários")
        }
    }
}

struct ScheduleTileView: View {
    let tile: Tile
    
    var body: some View {
        switch tile.element {
        case .empty:
            VStack {
                Spacer()
            }.frame(width: 64, height: 27)
        case .day(let day):
            VStack {
                Text(day.day)
                    .font(.footnote)
            }.frame(width: 64, height: 27)
        case .time(let time):
            VStack {
                Text(time.start)
                    .font(.footnote)
                Text(time.end)
                    .font(.footnote)
            }.frame(width: 64, height: 54)
        case .element(let element):
            VStack {
                Text(element.location)
                    .font(.footnote)
                Text(element.group)
                    .font(.caption2)
            }
            .frame(width: 64, height: 54)
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(lineWidth: 0.8)
                    .foregroundStyle(.blue)
            )
            .background(.blue.opacity(0.1))
            .clipShape(.rect(cornerRadius: 8))
        }
    }
}

struct ScheduleHorizontalView : View {
    let item: Horizontal
    let geometry: GeometryProxy
    
    var body: some View {
        switch item.element {
        case .day(let day):
            VStack {
                Text(day.day)
                    .font(.headline)
                    .foregroundStyle(.yellow)
            }
            .padding(.horizontal)
            .padding(.top)
        case .element(let space):
            HStack(spacing: 0) {
                VStack(spacing: 0) {
                    Text("08:30")
                        .font(.caption)
                        .fontWeight(.regular)
                    
                    Rectangle()
                        .frame(width: 24, height: 1.5)
                        .padding(.vertical, 4)
                        .foregroundStyle(.blue.opacity(0.5))
                    
                    Text("10:30")
                        .font(.caption)
                        .fontWeight(.regular)
                }
                .frame(width: 48)
                
                VStack(alignment: .leading, spacing: 0) {
                    Text(space.location)
                        .lineLimit(1)
                        .multilineTextAlignment(.leading)
                        .font(.caption)
                        .fontWeight(.regular)
                    
                    Rectangle()
                        .frame(height: 1.5)
                        .padding(.vertical, 4)
                        .foregroundStyle(.gray.opacity(0.3))
                    
                    HStack(spacing: 0) {
                        Text("TEC510")
                            .font(.caption)
                            .fontWeight(.regular)
                        
                        Text("Módulo 3")
                            .font(.caption)
                            .fontWeight(.regular)
                            .padding(.leading)
                        
                        Text("PAT34")
                            .font(.caption)
                            .fontWeight(.regular)
                            .padding(.leading)
                    }
                }
                .padding(.leading)
            }
            .padding(.horizontal, 8)
            .padding(.top, 1)
            .frame(height: 56, alignment: .leading)
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(lineWidth: 0.8)
                    .foregroundStyle(.blue)
                    .shadow(color: .blue, radius: 10)
            )
            .background(.blue.opacity(0.1))
            .clipShape(.rect(cornerRadius: 8))
            .padding(.horizontal)
        }
    }
}

#Preview {
    HomeScheduleView()
}
