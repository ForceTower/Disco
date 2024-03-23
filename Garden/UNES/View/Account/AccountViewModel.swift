//
//  AccountViewModel.swift
//  UNES
//
//  Created by João Paulo Santos Sena on 19/03/24.
//

import Combine
import Club
import KMPNativeCoroutinesCombine
import KMPNativeCoroutinesAsync
import SwiftUI
import PhotosUI

class AccountViewModel : ObservableObject {
    enum ImageState {
        case empty
        case loading(Progress)
        case success(Image)
        case failure(Error)
    }
    
    enum TransferError: Error {
        case importFailed
    }
    
    struct ProfileImage: Transferable {
        let image: Image
        let uiImage: UIImage
        let data: Data
        
        static var transferRepresentation: some TransferRepresentation {
            DataRepresentation(importedContentType: .image) { data in
#if canImport(AppKit)
                guard let nsImage = NSImage(data: data) else {
                    throw TransferError.importFailed
                }
                let image = Image(nsImage: nsImage)
                return ProfileImage(image: image)
#elseif canImport(UIKit)
                guard let uiImage = UIImage(data: data) else {
                    throw TransferError.importFailed
                }
                let image = Image(uiImage: uiImage)
                return ProfileImage(image: image, uiImage: uiImage, data: data)
#else
                throw TransferError.importFailed
#endif
            }
        }
    }
    
    @Published var imageSelection: PhotosPickerItem? = nil {
        didSet {
            if let imageSelection {
                let progress = loadTransferable(from: imageSelection)
                imageLoaded = false
                imageState = .loading(progress)
            } else {
                imageState = .empty
            }
        }
    }
    
    private let account: GetAccountUseCase
    private let user: ConnectedUserUseCase
    private let auth: ServiceAuthUseCase
    
    private var subscriptions = Set<AnyCancellable>()
    @Published private(set) var currentProfile: Profile? = nil
    @Published private(set) var currentAccount: ServiceAccount? = nil
    @Published private(set) var imageState: ImageState = .empty
    @Published var showImageCropper: Bool = false
    @Published var imageLoaded: Bool = true
    private(set) var selectedImage: UIImage? = nil
    @Published private(set) var tempImage: Image? = nil
    
    
    init(user: ConnectedUserUseCase = AppDIContainer.shared.resolve(),
         account: GetAccountUseCase = AppDIContainer.shared.resolve(),
         auth: ServiceAuthUseCase = AppDIContainer.shared.resolve()) {
        self.user = user
        self.account = account
        self.auth = auth
        fetchProfile()
        fetchAccount()
    }
    
    private func fetchProfile() {
        createPublisher(for: user.currentProfile())
            .receive(on: DispatchQueue.main)
            .sink { _ in
                
            } receiveValue: { [weak self] profile in
                self?.currentProfile = profile
            }
            .store(in: &subscriptions)
    }
    
    private func fetchAccount() {
        createPublisher(for: account.getAccount())
            .receive(on: DispatchQueue.main)
            .sink { completion in
                print("Received flow account completion \(completion)")
            } receiveValue: { [weak self] account in
                self?.currentAccount = account
            }
            .store(in: &subscriptions)
        
        createFuture(for: account.fetchAccountIfConnected())
            .receive(on: DispatchQueue.main)
            .sink { completion in
                print("Fetch account completion \(completion)")
            } receiveValue: { [weak self] account in
                self?.currentAccount = account
            }
            .store(in: &subscriptions)
    }
    
    func deleteAccount() {
        Task {
            try? await asyncFunction(for: auth.deleteAuthAndAccount())
        }
    }
    
    private func loadTransferable(from imageSelection: PhotosPickerItem) -> Progress {
        return imageSelection.loadTransferable(type: ProfileImage.self) { result in
            DispatchQueue.main.async { [weak self] in
                guard imageSelection == self?.imageSelection else {
                    print("Failed to get the selected item.")
                    return
                }
                switch result {
                case .success(let profileImage?):
                    self?.imageState = .success(profileImage.image)
                    self?.selectedImage = profileImage.uiImage
                    self?.showImageCropper = true
                case .success(nil):
                    self?.imageLoaded = true
                    self?.imageState = .empty
                case .failure(let error):
                    self?.imageLoaded = true
                    self?.imageState = .failure(error)
                }
            }
        }
    }
    
    func sendImageToServer(image: UIImage) {
        guard let data = image.jpegData(compressionQuality: 0.8) else {
            print("Failed to jped. lol")
            return
        }
        self.imageLoaded = false
        self.tempImage = Image(uiImage: image)
        
        Task {
            do {
                let base64 = data.base64EncodedString()
                try await doSendImageToServer(base64: base64)
                DispatchQueue.main.async { [weak self] in
                    self?.imageLoaded = true
                    self?.tempImage = nil
                }
            } catch {
                print("Failed to send \(error.localizedDescription)")
                DispatchQueue.main.async { [weak self] in
                    self?.imageLoaded = true
                    self?.tempImage = nil
                }
            }
        }
    }
    
    func cancelImageChange() {
        imageLoaded = true
        tempImage = nil
        selectedImage = nil
    }
    
    private func doSendImageToServer(base64: String) async throws {
        let _ = try await asyncFunction(for: account.changeProfilePicture(base64: base64.fixedBase64Format))
    }
}
