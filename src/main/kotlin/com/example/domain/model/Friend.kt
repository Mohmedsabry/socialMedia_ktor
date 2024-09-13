package com.example.domain.model

import java.time.LocalDate

data class Friend(
    val id: Int=0,
    val emailOfUser1: String,
    val user2: User,
    val isCloseFriend: Boolean,
    val acceptFriendDate: LocalDate?,
    val closeFriendDate: LocalDate?,
    val statues: String
)
