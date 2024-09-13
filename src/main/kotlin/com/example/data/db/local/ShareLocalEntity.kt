package com.example.data.db.local

data class ShareLocalEntity(
    val postId: Int,
    val comments: Int,
    val likes: Int,
    val shares: Int,
    val content: String,
    val email: String,
    val sharedPostId: Int,
    val dateOfShare: String,
    val type:String,
    val sharedContent:String
)
