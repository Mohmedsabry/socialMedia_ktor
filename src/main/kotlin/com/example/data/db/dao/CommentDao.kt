package com.example.data.db.dao

import com.example.data.db.DataBaseConnection
import com.example.data.db.entity.CommentsEntity
import com.example.data.dto.CommentDto
import com.example.data.mapper.toDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.ktorm.dsl.*
import java.time.LocalDate

object CommentDao {
    private val db = DataBaseConnection.database
    private val userDao = UserDao
    fun addComment(
        postId: Int,
        comment: String,
        email: String
    ): Int {
        val insertComment = db.insert(CommentsEntity) {
            set(CommentsEntity.postId, postId)
            set(CommentsEntity.comment, comment)
            set(CommentsEntity.email, email)
            set(CommentsEntity.dateOfComment, LocalDate.now())
        }
        return insertComment
    }

    fun updateComment(
        postId: Int,
        comment: String,
        email: String
    ): Int {
        return db.update(CommentsEntity) {
            where {
                it.postId eq postId and (it.email eq email)
            }
            set(it.comment, comment)
        }
    }

    fun deleteComment(postId: Int, email: String): Int {
        return db.delete(CommentsEntity) { it.postId eq postId and (email eq CommentsEntity.email) }
    }

    suspend fun getComments(postId: Int): List<CommentDto> {
        return withContext(Dispatchers.IO) {
            db.from(CommentsEntity).select()
                .where { CommentsEntity.postId eq postId }
                .map {
                    val user = async {
                        userDao.getUserFromDB(it[CommentsEntity.email]!!)
                    }.await()
                    CommentDto(
                        user = user!!.toDto(),
                        it[CommentsEntity.comment] ?: "",
                    )
                }
        }
    }
}