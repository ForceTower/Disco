//
//  AboutView.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 19/03/24.
//

import SwiftUI
import UniformTypeIdentifiers

struct AboutView: View {
    @AppStorage("settings_device_local_id") private var deviceId = "unknown_device_id"
    @State private var showInfoCopied = false
    
    var body: some View {
        VStack {
            List {
                Section {
                    Text(Bundle.main.appName)
                    HStack {
                        Text("Versão")
                        Spacer()
                        Text(Bundle.main.appVersionLong)
                            .foregroundStyle(.secondary)
                    }
                    HStack {
                        Text("Compilação")
                        Spacer()
                        Text(Bundle.main.appBuild)
                            .foregroundStyle(.secondary)
                    }
                    HStack {
                        Text("ID")
                        Spacer()
                        Text(deviceId)
                            .foregroundStyle(.secondary)
                    }
                    HStack {
                        Text("Dispositivo")
                        Spacer()
                        Text(modelIdentifier())
                            .foregroundStyle(.secondary)
                    }
                    HStack {
                        Text("Idioma")
                        Spacer()
                        Text(Bundle.main.language)
                            .foregroundStyle(.secondary)
                    }
                    Button {
                        let info = """
                    UNES \(Bundle.main.appVersionLong)(\(Bundle.main.appBuild)) - \(modelIdentifier()) \(Bundle.main.language)
                    ID Instalação: \(deviceId)
                    """
                        UIPasteboard.general.setValue(info,
                                                      forPasteboardType: UTType.plainText.identifier)
                        showInfoCopied = true
                    } label: {
                        Text("Copiar informações")
                    }
                } header: {
                    HStack {
                        Spacer()
                        Image(.coloredLogo)
                            .resizable()
                            .scaledToFit()
                            .frame(height: 200)
                        Spacer()
                    }
                    .padding(.bottom)
                }
            }
        }
        .background(Color(UIColor.systemGroupedBackground))
        .navigationTitle("Sobre o UNES")
        .navigationBarTitleDisplayMode(.inline)
        .alert("Dados copiados!", isPresented: $showInfoCopied) {
            Button("OK") {
                showInfoCopied = false
            }
        }
    }
    
    func modelIdentifier() -> String {
        if let simulatorModelIdentifier = ProcessInfo().environment["SIMULATOR_MODEL_IDENTIFIER"] { return simulatorModelIdentifier }
        var sysinfo = utsname()
        uname(&sysinfo)
        return String(bytes: Data(bytes: &sysinfo.machine, count: Int(_SYS_NAMELEN)), encoding: .ascii)!.trimmingCharacters(in: .controlCharacters)
    }
}

#Preview {
    NavigationStack {
        AboutView()
    }
}
