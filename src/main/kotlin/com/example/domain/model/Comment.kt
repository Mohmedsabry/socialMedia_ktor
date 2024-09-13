package com.example.domain.model

import java.time.LocalDate

data class Comment(
    val postId: Int,
    val email: String,
    val content: String,
    val dateOfComment: LocalDate,
)
