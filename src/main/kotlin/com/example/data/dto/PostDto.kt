package com.example.data.dto

import com.example.domain.model.TypeOfPost
import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val editor: String,
    val content: String,
    val type: Int = TypeOfPost.PUBLIC.ordinal
)
