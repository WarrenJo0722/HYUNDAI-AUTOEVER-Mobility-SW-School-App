//
//  ContentView.swift
//  SocialMediaApp
//
//  Created by Kihwan Jo on 2/11/25.
//

import SwiftUI
import FirebaseFirestore

struct FeedView: View {
    @State private var posts: [Post] = []
    @State private var isShowingPostCreation = false  // 새 글 작성 화면을 띄울지 여부

    var body: some View {
        NavigationView {
            List(posts) { post in
                VStack(alignment: .leading) {
                    Text(post.text)
                    if let imageUrl = post.imageUrl {
                        AsyncImage(url: URL(string: imageUrl)) { image in
                            image.resizable()
                                .scaledToFill()  // 이미지 크롭
                                .frame(width: UIScreen.main.bounds.width - 40, height: 300)  // 가로 전체 너비로 설정
//                                .frame(width: 100, height: 100)  // 크기 설정
                                .clipped()  // 프레임을 넘은 부분 잘라내기
                        } placeholder: {
                            ProgressView()
                        }
//                        .frame(width: 100, height: 100)
                    }
                    Text("작성일: \(post.timestamp, formatter: Self.dateFormatter)")
                        .font(.subheadline)
                        .foregroundColor(.gray)
                }
//                .padding([.leading, .trailing], 10)
//                .listRowSeparator(.hidden)  // 경계선 제거
            }
            .listStyle(PlainListStyle())
//            .listStyle(InsetGroupedListStyle())
            .navigationTitle("피드")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        isShowingPostCreation = true
                    }) {
                        Image(systemName: "plus")
                    }
                }
            }
            .onAppear {
                fetchPosts()
            }
            .sheet(isPresented: $isShowingPostCreation) {
                PostCreationView(isPresented: $isShowingPostCreation, posts: $posts)
            }
        }
    }

    static let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium  // 예: "2024. 2. 11."
        formatter.timeStyle = .short   // 예: "오후 3:45"
        return formatter
    }()
    
    func fetchPosts() {
        let db = Firestore.firestore()
        let postsRef = db.collection("posts")
        
        postsRef.order(by: "timestamp", descending: true).getDocuments { snapshot, error in
            if let error = error {
                print("게시물 가져오기 실패: \(error.localizedDescription)")
                return
            }
            
            posts = snapshot?.documents.compactMap { doc -> Post? in
                try? doc.data(as: Post.self)
            } ?? []
        }
    }
}

#Preview {
    FeedView()
}
