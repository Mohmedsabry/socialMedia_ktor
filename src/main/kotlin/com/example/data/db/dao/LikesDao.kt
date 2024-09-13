package com.example.data.db.dao

import com.example.data.db.DataBaseConnection
import com.example.data.db.entity.LikesEntity
import org.ktorm.dsl.*

object LikesDao {
    val db = DataBaseConnection.database
    fun addLikes(postId: Int, email: String): Int {
        return db.insert(LikesEntity) {
            set(it.postId, postId)
            set(it.email, email)
        }
    }

    fun deleteLikes(postId: Int, email: String): Int {
        return db.delete(LikesEntity) {
            it.postId eq postId and (it.email eq email)
        }
    }

    fun getLiked(postId: Int, email: String): Int? {
        val date = db.from(LikesEntity).select().where {
            LikesEntity.postId eq postId and (email eq LikesEntity.email)
        }.map {
            it[LikesEntity.postId]!!
        }
        return date.firstOrNull()
    }
}