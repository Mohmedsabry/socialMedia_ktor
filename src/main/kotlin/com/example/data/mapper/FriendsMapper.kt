package com.example.data.mapper

import com.example.data.dto.FriendsDto
import com.example.domain.model.Friend

fun Friend.toDto() = FriendsDto(
    user2.toDto(),
    this.statues,
    this.isCloseFriend,
    this.acceptFriendDate.toString()
)