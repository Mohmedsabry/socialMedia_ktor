package com.example.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SendPost(
    val postId: Int,
    val user: UserDto,
    val likes: Int,
    val comments: Int,
    val shares: Int,
    val content: String,
    val type: String,
    val isShared: Boolean = false,
    val isLiked: Boolean,
    val sharedContent: String
)
