package com.example.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class FriendsDto(
    val user: UserDto,
    val statues: String,
    val isClose: Boolean,
    val dateOfAcceptFriend: String?
)
