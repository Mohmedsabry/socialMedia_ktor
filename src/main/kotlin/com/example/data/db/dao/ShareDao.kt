package com.example.data.db.dao

import com.example.data.db.DataBaseConnection
import com.example.data.db.entity.SharesEntity
import com.example.data.db.local.ShareLocalEntity
import org.ktorm.dsl.*
import java.time.LocalDate

object ShareDao {
    private val db = DataBaseConnection.database
    fun addShare(
        postId: Int,
        content: String,
        email: String
    ): Int {
        val insertShare = db.insert(SharesEntity) {
            set(SharesEntity.postId, postId)
            set(SharesEntity.content, content)
            set(SharesEntity.email, email)
            set(SharesEntity.dateOfShare, LocalDate.now())
        }
        return insertShare
    }

    fun updateShare(
        postId: Int,
        content: String,
        email: String
    ): Int {
        return db.update(SharesEntity) {
            where {
                it.postId eq postId and (it.email eq email)
            }
            set(it.content, content)
        }
    }

    fun deleteShare(postId: Int, email: String): Int {
        return db.delete(SharesEntity) { it.postId eq postId and (email eq SharesEntity.email) }
    }

    fun getShares(email: String): List<ShareLocalEntity> {
        return db.from(SharesEntity).select().where(
            email eq SharesEntity.email
        ).map {
            ShareLocalEntity(
                postId = it[SharesEntity.postId]!!,
                email = email,
                comments = it[SharesEntity.comments]!!,
                likes = it[SharesEntity.likes]!!,
                shares = it[SharesEntity.shares]!!,
                sharedContent = it[SharesEntity.content]!!,
                sharedPostId = it[SharesEntity.id]!!,
                dateOfShare = it[SharesEntity.dateOfShare]!!.toString(),
                type = it[SharesEntity.type]!!,
                content = ""
            )
        }
    }

    fun getPostById(id: Int,content:String): ShareLocalEntity {
        return db.from(SharesEntity).select().where(id eq SharesEntity.id)
            .map {
                ShareLocalEntity(
                    postId = it[SharesEntity.postId]!!,
                    email = it[SharesEntity.email]!!,
                    comments = it[SharesEntity.comments]!!,
                    likes = it[SharesEntity.likes]!!,
                    shares = it[SharesEntity.shares]!!,
                    content = content,
                    sharedPostId = it[SharesEntity.id]!!,
                    dateOfShare = it[SharesEntity.dateOfShare]!!.toString(),
                    type = it[SharesEntity.type]!!,
                    sharedContent = it[SharesEntity.content]!!
                )
            }.first()
    }

    fun updateLikes(id: Int, value: Int): Int {
        return db.update(SharesEntity) {
            set(SharesEntity.likes, SharesEntity.likes + value)
            where {
                it.id eq id
            }
        }
    }

    fun updateComments(id: Int, value: Int): Int {
        return db.update(SharesEntity) {
            set(SharesEntity.comments, SharesEntity.comments + value)
            where {
                it.id eq id
            }
        }
    }

    fun updateShares(id: Int, value: Int): Int {
        return db.update(SharesEntity) {
            set(SharesEntity.shares, SharesEntity.shares + value)
            where {
                it.id eq id
            }
        }
    }
}