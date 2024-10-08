package com.example.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(val user: UserDto, val comment: String)
