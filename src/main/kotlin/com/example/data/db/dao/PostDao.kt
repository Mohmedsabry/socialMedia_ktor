package com.example.data.db.dao

import com.example.data.db.DataBaseConnection
import com.example.data.db.entity.PostEntity
import com.example.domain.model.Post
import com.example.domain.model.TypeOfPost
import com.example.domain.model.User
import com.example.domain.util.Result
import org.ktorm.dsl.*
import java.time.LocalDate

object PostDao {
    val db = DataBaseConnection.database
    private val userDao = UserDao
    fun addPost(post: Post): Int {
        val op = db.insert(PostEntity) {
            set(PostEntity.type, post.type.title)
            set(PostEntity.editor, post.user.email)
            set(PostEntity.likes, post.likes)
            set(PostEntity.shared, post.shares)
            set(PostEntity.comments, post.comments)
            set(PostEntity.content, post.content)
            set(PostEntity.dateOfCreate, LocalDate.now())
        }
        return op
    }

    fun getPosts(): List<Post> {
        val posts = db.from(PostEntity).select()
            .map {
                val postId = it[PostEntity.postId]!!
                val editor = it[PostEntity.editor]!!
                val content = it[PostEntity.content]!!
                val likes = it[PostEntity.likes]!!
                val comments = it[PostEntity.comments]!!
                val shares = it[PostEntity.shared]!!
                val type = it[PostEntity.type]!!
                val user = when (val res = userDao.getUser(editor)) {
                    is Result.Failure -> {
                        User(0, "", "", "", 0f, "", "", LocalDate.now(), LocalDate.now())
                    }

                    is Result.Success -> res.data
                }
                Post(
                    postId = postId,
                    user = user,
                    likes,
                    comments,
                    shares,
                    content,
                    type = if (type == "public") TypeOfPost.PUBLIC else if (type == "private") TypeOfPost.PRIVATE else TypeOfPost.ONLY_FRIENDS,
                    isLiked = false,
                    sharedContent = ""
                )
            }
        return posts
    }

    fun deletePost(id: Int): Int {
        val op = db.delete(PostEntity) {
            it.postId eq id
        }
        println(op)
        return op
    }

    fun updatePost(postId: Int, content: String): Int {
        val op = db.update(PostEntity) {
            set(PostEntity.content, content)
            where { it.postId eq postId }
        }
        return op
    }

    fun getPostById(postId: Int): Post? {
        val post = db.from(PostEntity).select()
            .where(PostEntity.postId eq postId)
            .map {
                val editor = it[PostEntity.editor]!!
                val content = it[PostEntity.content]!!
                val likes = it[PostEntity.likes]!!
                val comments = it[PostEntity.comments]!!
                val shares = it[PostEntity.shared]!!
                val type = it[PostEntity.type]!!
                val user = when (val res = userDao.getUser(editor)) {
                    is Result.Failure -> {
                        User(0, "", "", "", 0f, "", "", LocalDate.now(), LocalDate.now())
                    }

                    is Result.Success -> res.data
                }
                Post(
                    postId = postId,
                    user,
                    likes,
                    comments,
                    shares,
                    content,
                    type = if (type == "public") TypeOfPost.PUBLIC else if (type == "private") TypeOfPost.PRIVATE else TypeOfPost.ONLY_FRIENDS,
                    isLiked = false,
                    sharedContent = ""
                )
            }
        return post.firstOrNull()
    }

    fun updateLikes(value: Int, postId: Int): Int {
        val op = db.update(PostEntity) {
            set(it.likes, it.likes + value)
            where {
                it.postId eq postId
            }
        }
        return op
    }

    fun updateComments(value: Int, postId: Int): Int {
        val op = db.update(PostEntity) {
            set(it.comments, it.comments + value)
            where {
                it.postId eq postId
            }
        }
        return op
    }

    fun updateShares(value: Int, postId: Int): Int {
        val op = db.update(PostEntity) {
            set(it.shared, it.shared + value)
            where {
                it.postId eq postId
            }
        }
        return op
    }

    fun getPostEditor(postId: Int): String {
        return db.from(PostEntity).select(PostEntity.editor).where(postId eq PostEntity.postId)
            .map {
                it[PostEntity.editor]!!
            }.first()
    }
}