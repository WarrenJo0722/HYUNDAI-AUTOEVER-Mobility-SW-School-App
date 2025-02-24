//
//  PostCreationView.swift
//  SocialMediaApp
//
//  Created by Kihwan Jo on 2/11/25.
//

import SwiftUI
import PhotosUI
import FirebaseFirestore
import FirebaseStorage

struct PostCreationView: View {
    @Binding var isPresented: Bool  // 모달을 닫기 위한 바인딩 변수
    @Binding var posts: [Post]  // FeedView에서 전달받는 posts 배열

    @State private var postText: String = ""
    @State private var selectedImage: UIImage?  // 선택한 이미지 저장
    @State private var selectedItem: PhotosPickerItem?  // 사진 선택 아이템
    @State private var isUploading: Bool = false  // 이미지 업로드 중 여부

    var body: some View {
        NavigationView {
            VStack {
                TextField("내용을 입력하세요...", text: $postText)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .padding()

                // 사진 선택 버튼
                PhotosPicker(selection: $selectedItem, matching: .images) {
                    Text("앨범에서 사진 선택")
                        .foregroundColor(.blue)
                        .padding()
                }
                .onChange(of: selectedItem) { oldItem, newItem in
                    loadSelectedImage(from: newItem)
                }

                // 선택한 이미지 표시
                if let image = selectedImage {
                    Image(uiImage: image)
                        .resizable()
                        .scaledToFit()
                        .frame(height: 200)
                        .cornerRadius(10)
                        .padding()
                }
                
                if isUploading {
                    ProgressView("업로드 중...")
                        .progressViewStyle(CircularProgressViewStyle())
                        .padding()
                }

                Button("게시") {
                    addNewPost()
                }
                .padding()
                .disabled(isUploading)  // 업로드 중일 때 버튼 비활성화

                Spacer()
            }
            .navigationTitle("새 글 작성")
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("취소") {
                        isPresented = false
                    }
                }
            }
        }
    }

    /// 선택한 사진을 UIImage로 변환하여 저장
    private func loadSelectedImage(from item: PhotosPickerItem?) {
        guard let item else { return }
        
        Task {
            if let data = try? await item.loadTransferable(type: Data.self),
               let image = UIImage(data: data) {
                selectedImage = image
            }
        }
    }

    /// Firebase Storage에 이미지를 업로드하고 URL을 반환하는 함수
    func uploadImageToFirebase(_ image: UIImage) async -> String? {
        guard let imageData = image.jpegData(compressionQuality: 0.8) else {
            print("이미지 데이터를 가져오는 데 실패했습니다.")
            return nil
        }

        let storageRef = Storage.storage().reference()
        let fileName = UUID().uuidString // 고유한 파일 이름 생성
        let imageRef = storageRef.child("images/\(fileName).jpg")
        
        do {
            // 비동기적으로 이미지를 업로드
            let _ = try await imageRef.putDataAsync(imageData)
            // 업로드가 완료되면 다운로드 URL을 가져옴
            let downloadURL = try await imageRef.downloadURL()
            return downloadURL.absoluteString
        } catch {
            print("이미지 업로드 실패: \(error.localizedDescription)")
            return nil
        }
    }

    /// 새 게시물 추가
    func addNewPost() {
        guard !postText.isEmpty else { return }
        isUploading = true
        
        Task {
            var imageUrl: String? = nil
            
            if let selectedImage {
                imageUrl = await uploadImageToFirebase(selectedImage)
            }
            
            let newPost = Post(id: UUID().uuidString, text: postText, timestamp: Date(), imageUrl: imageUrl)
            
            let db = Firestore.firestore()
            let postsRef = db.collection("posts")
            
            do {
                // Firestore에 데이터를 비동기적으로 저장하는 방법
                try await postsRef.document(newPost.id).setData([
                    "id": newPost.id,
                    "text": newPost.text,
                    "timestamp": newPost.timestamp,
                    "imageUrl": newPost.imageUrl ?? ""
                ])
                
                // 성공적으로 게시물이 추가되면 posts 배열을 업데이트
                DispatchQueue.main.async {
                    posts.insert(newPost, at: 0)  // 최신 글을 맨 위에 추가
                    isPresented = false  // 모달 닫기
                }
            } catch {
                print("Firestore에 게시물 저장 실패: \(error.localizedDescription)")
            }
            
            isUploading = false
        }
    }
}

#Preview {
    PostCreationView(isPresented: .constant(false), posts: .constant([]))
}
