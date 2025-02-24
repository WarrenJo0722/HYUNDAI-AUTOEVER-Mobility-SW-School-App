//
//  Post.swift
//  SocialMediaApp
//
//  Created by Kihwan Jo on 2/11/25.
//

import Foundation

struct Post: Identifiable, Codable {
    var id: String
    var text: String
    var timestamp: Date
    var imageUrl: String?
}
