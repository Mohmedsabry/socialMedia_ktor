package com.example.data.db.dao

import com.example.data.db.DataBaseConnection
import com.example.data.db.entity.FriendsEntity
import com.example.data.db.local.FriendLocalEntity
import org.ktorm.dsl.*
import java.time.LocalDate

object FriendsDao {
    private val db = DataBaseConnection.database
    fun getAllFriends(email: String): List<FriendLocalEntity> {
        return db.from(FriendsEntity).select()
            .where(FriendsEntity.userOneEmail eq email or (FriendsEntity.userTwoEmail eq email))
            .map {
                val user1 = it[FriendsEntity.userOneEmail]!!
                val user2 = it[FriendsEntity.userTwoEmail]!!
                val statues = it[FriendsEntity.statues]!!
                val isClose = it[FriendsEntity.closeFriend]!!
                val accept = it[FriendsEntity.acceptFriendDate]
                val close = it[FriendsEntity.closeFriendDate]
                FriendLocalEntity(
                    emailOfUser1 = user1,
                    emailOfUser2 = user2,
                    isCloseFriend = isClose,
                    acceptFriendDate = accept,
                    closeFriendDate = close,
                    statues = statues
                )
            }
    }

    fun updateStatues(email1: String, email2: String, statues: String): Int {
        return if (statues == "Accept")
            db.update(FriendsEntity) {
                set(it.statues, statues)
                set(it.acceptFriendDate, LocalDate.now())
                where {
                    (it.userOneEmail eq email1 or (it.userOneEmail eq email2)) and (
                            (it.userTwoEmail eq email1 or (it.userTwoEmail eq email2))
                            )
                }
            }
        else db.delete(FriendsEntity) {
            (it.userOneEmail eq email1 or (it.userOneEmail eq email2)) and (
                    (it.userTwoEmail eq email1 or (it.userTwoEmail eq email2))
                    )
        }
    }

    fun updateClose(email1: String, email2: String, isClose: Boolean): Int {
        return db.update(FriendsEntity) {
            set(it.closeFriend, isClose)
            where {
                (it.userOneEmail eq email1 or (it.userOneEmail eq email2)) and (
                        (it.userTwoEmail eq email1 or (it.userTwoEmail eq email2))
                        )
            }
        }
    }

    fun addFriend(email1: String, email2: String): Int {
        return db.insert(FriendsEntity) {
            set(it.userOneEmail, email1)
            set(it.userTwoEmail, email2)
            set(it.closeFriend, false)
            set(it.statues, "Pending")
            set(it.acceptFriendDate, null)
            set(it.closeFriendDate, null)
        }
    }
    fun getFriendsRequest(email: String):List<FriendLocalEntity>{
        return db.from(FriendsEntity).select()
            .where(FriendsEntity.userTwoEmail eq email)
            .map {
                val user1 = it[FriendsEntity.userOneEmail]!!
                val user2 = it[FriendsEntity.userTwoEmail]!!
                val statues = it[FriendsEntity.statues]!!
                val isClose = it[FriendsEntity.closeFriend]!!
                val accept = it[FriendsEntity.acceptFriendDate]
                val close = it[FriendsEntity.closeFriendDate]
                FriendLocalEntity(
                    emailOfUser1 = user1,
                    emailOfUser2 = user2,
                    isCloseFriend = isClose,
                    acceptFriendDate = accept,
                    closeFriendDate = close,
                    statues = statues
                )
            }
    }
}