package com.example.domain.model

data class Post(
    val postId: Int,
    val user: User,
    val likes: Int,
    val comments: Int,
    val shares: Int,
    val content: String,
    val type: TypeOfPost = TypeOfPost.PUBLIC,
    val isShared: Boolean = false,
    val isLiked:Boolean,
    val sharedContent:String
)

enum class TypeOfPost(val title: String) {
    PUBLIC("public"),
    PRIVATE("private"),
    ONLY_FRIENDS("only friends")
}
