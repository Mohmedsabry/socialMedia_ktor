package com.example.data.repository

import com.example.data.db.dao.FriendsDao
import com.example.data.db.dao.UserDao
import com.example.data.util.FriendsError
import com.example.domain.model.Friend
import com.example.domain.model.User
import com.example.domain.repository.FriendsRepository
import com.example.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.time.LocalDate

class FriendsRepositoryImpl : FriendsRepository {
    private val userController = UserDao
    private val friendController = FriendsDao
    override suspend fun getFriendsForUser(
        email: String
    ): Result<List<Friend>, FriendsError> {
        return withContext(Dispatchers.IO) {
            try {
                val friendsLocal = friendController.getAllFriends(email)
                val friends = friendsLocal.map {
                    val userEmail = if (email == it.emailOfUser1) it.emailOfUser2 else it.emailOfUser1
                    val user = async {
                        userController.getUser(userEmail)
                    }.await()
                    Friend(
                        emailOfUser1 = email,
                        user2 = when (user) {
                            is Result.Failure -> User(0, "", "", "", 0f, "", "", LocalDate.now(), LocalDate.now())
                            is Result.Success -> user.data
                        },
                        isCloseFriend = it.isCloseFriend,
                        acceptFriendDate = it.acceptFriendDate,
                        closeFriendDate = it.closeFriendDate,
                        statues = it.statues
                    )
                }
                if (friends.isNotEmpty()) Result.Success(friends)
                else Result.Failure(FriendsError.THERE_IS_NO_DATA)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(FriendsError.UN_KNOWN)
            }
        }
    }

    override suspend fun getRequestsForUser(email: String): Result<List<Friend>, FriendsError> {
        return withContext(Dispatchers.IO) {
            try {
                val friendsLocal = friendController.getFriendsRequest(email)
                val friends = friendsLocal.map {
                    val userEmail = if (email == it.emailOfUser1) it.emailOfUser2 else it.emailOfUser1
                    val user = async {
                        userController.getUser(userEmail)
                    }.await()
                    Friend(
                        emailOfUser1 = email,
                        user2 = when (user) {
                            is Result.Failure -> User(0, "", "", "", 0f, "", "", LocalDate.now(), LocalDate.now())
                            is Result.Success -> user.data
                        },
                        isCloseFriend = it.isCloseFriend,
                        acceptFriendDate = it.acceptFriendDate,
                        closeFriendDate = it.closeFriendDate,
                        statues = it.statues
                    )
                }
                if (friends.isNotEmpty()) Result.Success(friends)
                else Result.Failure(FriendsError.THERE_IS_NO_DATA)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(FriendsError.UN_KNOWN)
            }
        }
    }

    override suspend fun updateFriendShip(
        user1: String,
        user2: String,
        statues: String
    ): Result<Unit, FriendsError> {
        return withContext(Dispatchers.IO) {
            try {
                val op = friendController.updateStatues(user1, user2, statues)
                println("op $op")
                if (op == 0) return@withContext Result.Failure(FriendsError.THERE_IS_NO_DATA)
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(FriendsError.UN_KNOWN)
            }
        }
    }

    override suspend fun addClose(
        user1: String,
        user2: String
    ): Result<Unit, FriendsError> {
        return withContext(Dispatchers.IO) {
            try {
                val op = friendController.updateClose(
                    user1, user2, true
                )
                if (op != 1) return@withContext Result.Failure(FriendsError.THERE_IS_NO_DATA)
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(FriendsError.UN_KNOWN)
            }
        }
    }

    override suspend fun removeClose(
        user1: String,
        user2: String
    ): Result<Unit, FriendsError> {
        return withContext(Dispatchers.IO) {
            try {
                val op = friendController.updateClose(
                    user1, user2, false
                )
                if (op != 1) return@withContext Result.Failure(FriendsError.THERE_IS_NO_DATA)
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(FriendsError.UN_KNOWN)
            }
        }
    }

    override suspend fun addFriend(
        user1: String,
        user2: String
    ): Result<Unit, FriendsError> {
        return withContext(Dispatchers.IO) {
            try {
                val op = friendController.addFriend(
                    user1, user2
                )
                if (op != 1) return@withContext Result.Failure(FriendsError.THERE_IS_NO_DATA)
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(FriendsError.UN_KNOWN)
            }
        }
    }
}