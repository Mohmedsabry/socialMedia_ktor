package com.example.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SharePostDto(
    val postId: Int,
    val editor: String,
    val content: String,
    val isShared:Boolean
)
