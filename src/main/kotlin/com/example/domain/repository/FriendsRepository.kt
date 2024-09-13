package com.example.domain.repository

import com.example.data.util.FriendsError
import com.example.domain.model.Friend
import com.example.domain.util.Result

interface FriendsRepository {
    suspend fun getFriendsForUser(email: String): Result<List<Friend>, FriendsError>
    suspend fun getRequestsForUser(email: String): Result<List<Friend>, FriendsError>
    suspend fun updateFriendShip(user1: String, user2: String, statues: String): Result<Unit, FriendsError>
    suspend fun addClose(user1: String, user2: String): Result<Unit, FriendsError>
    suspend fun removeClose(user1: String, user2: String): Result<Unit, FriendsError>
    suspend fun addFriend(user1: String, user2: String): Result<Unit, FriendsError>
}