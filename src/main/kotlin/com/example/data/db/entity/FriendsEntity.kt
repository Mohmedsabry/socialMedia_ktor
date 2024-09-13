package com.example.data.db.entity

import org.ktorm.schema.*

object FriendsEntity : Table<Nothing>("friends") {
    val id = int("id").primaryKey()
    val userOneEmail = varchar("user1")
    val userTwoEmail = varchar("user2")
    val statues = varchar("statues")
    val closeFriend = boolean("close")
    val acceptFriendDate = date("AcceptFriendShip")
    val closeFriendDate = date("CloseFriend")
}