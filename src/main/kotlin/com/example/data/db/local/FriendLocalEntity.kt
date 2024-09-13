package com.example.data.db.local

import java.time.LocalDate

data class FriendLocalEntity(
    val id: Int = 0,
    val emailOfUser1: String,
    val emailOfUser2: String,
    val isCloseFriend: Boolean,
    val acceptFriendDate: LocalDate?,
    val closeFriendDate: LocalDate?,
    val statues: String
)
