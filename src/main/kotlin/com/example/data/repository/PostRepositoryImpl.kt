package com.example.data.repository

import com.example.data.db.dao.*
import com.example.data.dto.CommentDto
import com.example.data.dto.SharePostDto
import com.example.data.mapper.toPost
import com.example.data.util.PostError
import com.example.domain.model.Post
import com.example.domain.model.User
import com.example.domain.repository.PostRepository
import com.example.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.time.LocalDate

class PostRepositoryImpl : PostRepository {
    private val postController = PostDao
    private val shareController = ShareDao
    private val userController = UserDao
    private val commentController = CommentDao
    private val likesController = LikesDao
    override suspend fun addPost(post: Post): Result<Unit, PostError> {
        return withContext(Dispatchers.IO) {
            try {
                val op = postController.addPost(post)
                if (op == 1) Result.Success(Unit)
                else Result.Failure(PostError.NO_CONNECTION)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(PostError.UN_KNOWN)
            }
        }
    }

    override suspend fun updatePostLikes(
        postId: Int,
        value: Int,
        email: String,
        isShared: Boolean
    ): Result<Unit, PostError> {
        return withContext(Dispatchers.IO) {
            try {
                println("isShared $isShared $value")
                if (!isShared) {
                    val op = async { postController.updateLikes(value, postId) }.await()
                    val op2 = async {
                        if (value > 0) likesController.addLikes(postId, email)
                        else likesController.deleteLikes(postId, email)
                    }.await()
                    if (op == 1 && op2 == 1) return@withContext Result.Success(Unit)
                } else {
                    val op = shareController.updateLikes(
                        postId,
                        value
                    )
                    if (op == 1) return@withContext Result.Success(Unit)
                }
                Result.Failure(PostError.NO_CONNECTION)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(PostError.UN_KNOWN)
            }
        }
    }

    override suspend fun getAllPosts(email: String): Result<List<Post>, PostError> {
        return withContext(Dispatchers.IO) {
            try {
                var posts = postController.getPosts()
                posts = posts.map {
                    val isLiked = likesController.getLiked(it.postId, email)
                    if (isLiked == null) it else it.copy(isLiked = true)
                }
                if (posts.isEmpty()) Result.Failure(PostError.THERE_IS_NO_DATA)
                else Result.Success(posts)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(PostError.UN_KNOWN)
            }
        }
    }

    override suspend fun updatePost(postId: Int, content: String): Result<Unit, PostError> {
        return withContext(Dispatchers.IO) {
            try {
                val op = postController.updatePost(postId = postId, content = content)
                if (op != 1) Result.Failure(PostError.NO_CONNECTION)
                else {
                    Result.Success(Unit)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(PostError.UN_KNOWN)
            }
        }
    }

    override suspend fun deletePost(postId: Int): Result<Unit, PostError> {
        return withContext(Dispatchers.IO) {
            try {
                val op = postController.deletePost(postId)
                if (op == 1) Result.Success(Unit)
                else Result.Failure(PostError.THERE_IS_NO_DATA)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(PostError.UN_KNOWN)
            }
        }
    }

    override suspend fun getPostById(
        postId: Int,
        isShared: Boolean
    ): Result<Post, PostError> {
        return withContext(Dispatchers.IO) {
            try {
                if (!isShared) {
                    val post = postController.getPostById(postId = postId)
                    if (post == null) Result.Failure(PostError.THERE_IS_NO_DATA)
                    else Result.Success(post)
                } else {
                    val post = async { postController.getPostById(postId) }.await()
                    val user = when (val res = userController.getUser(post?.user?.email ?: "")) {
                        is Result.Failure -> {
                            User(0, "", "", "", 0f, "", "", LocalDate.now(), LocalDate.now())
                        }

                        is Result.Success -> res.data
                    }
                    val isLiked = likesController.getLiked(postId, user.email)
                    val sentPost = shareController.getPostById(postId, post?.content ?: "")
                        .toPost(user, isLiked != null)
                    Result.Success(sentPost)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(PostError.UN_KNOWN)
            }
        }
    }

    override suspend fun sharePost(
        sharePostDto: SharePostDto
    ): Result<Unit, PostError> {
        return withContext(Dispatchers.IO) {
            try {
                val insert = async {
                    shareController.addShare(
                        sharePostDto.postId,
                        sharePostDto.content,
                        sharePostDto.editor
                    )
                }.await()
                if (insert != 1) return@withContext Result.Failure(PostError.NO_CONNECTION)
                if (sharePostDto.isShared) {
                    val post = shareController.updateShares(
                        sharePostDto.postId,
                        1
                    )
                    if (post != 1) return@withContext Result.Failure(PostError.NO_CONNECTION)
                } else {
                    val post = postController.updateShares(
                        1,
                        postId = sharePostDto.postId
                    )
                    if (post != 1) return@withContext Result.Failure(PostError.NO_CONNECTION)
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(PostError.UN_KNOWN)
            }
        }
    }

    override suspend fun updateSharePostContent(
        postId: Int,
        content: String,
        email: String
    ): Result<Unit, PostError> {
        return withContext(Dispatchers.IO) {
            try {
                val share = shareController.updateShare(
                    postId, content, email
                )
                if (share != 1) return@withContext Result.Failure(PostError.NO_CONNECTION)
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(PostError.UN_KNOWN)
            }
        }
    }

    override suspend fun getMyPosts(
        email: String
    ): Result<List<Post>, PostError> {
        return withContext(Dispatchers.IO) {
            try {
                val postsList = async { postController.getPosts() }.await()
                val sharesList = async {
                    shareController.getShares(email)
                }.await()
                val posts = sharesList.map {
                    val user = postsList.find { post -> it.postId == post.postId }?.user
                    val likes = async { likesController.getLiked(it.postId, it.email) }.await()
                    val isLiked = likes != null
                    val content = async {
                        postsList.find { post ->
                            it.postId == post.postId
                        }?.content ?: ""
                    }.await()
                    println("content $content")
                    it.toPost(user!!, isLiked)
                        .copy(
                            content = content
                        )
                } + postsList.map {
                    val isLiked = likesController.getLiked(it.postId, it.user.email)
                    if (isLiked == null) it else it.copy(isLiked = true)
                }.filter { it.user.email == email }
                Result.Success(posts)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(PostError.UN_KNOWN)
            }
        }
    }

    override suspend fun deleteSharedPost(
        postId: Int,
        email: String
    ): Result<Unit, PostError> {
        return withContext(Dispatchers.IO) {
            try {
                val deleted = shareController.deleteShare(postId, email)
                if (deleted != 1) return@withContext Result.Failure(PostError.NO_CONNECTION)
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(PostError.UN_KNOWN)
            }
        }
    }

    override suspend fun addComment(
        postId: Int,
        comment: String,
        isShared: Boolean,
        email: String
    ): Result<Unit, PostError> {
        return withContext(Dispatchers.IO) {
            try {
                val commented = async { commentController.addComment(postId, comment, email) }
                if (commented.await() != 1) return@withContext Result.Failure(PostError.NO_CONNECTION)
                if (isShared) {
                    val shared = shareController.updateComments(postId, 1)
                    if (shared != 1) return@withContext Result.Failure(PostError.THERE_IS_NO_DATA)
                } else {
                    val post = postController.updateComments(1, postId)
                    if (post != 1) return@withContext Result.Failure(PostError.THERE_IS_NO_DATA)
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(PostError.UN_KNOWN)
            }
        }
    }

    override suspend fun updateComment(
        postId: Int,
        comment: String,
        email: String
    ): Result<Unit, PostError> {
        return withContext(Dispatchers.IO) {
            try {
                val update = commentController.updateComment(
                    postId, comment, email
                )
                if (update != 1) return@withContext Result.Failure(PostError.NO_CONNECTION)
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(PostError.UN_KNOWN)
            }
        }
    }

    override suspend fun getCommentsForPost(
        postId: Int
    ): Result<List<CommentDto>, PostError> {
        return withContext(Dispatchers.IO) {
            try {
                val comments = commentController.getComments(postId)
                Result.Success(comments)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(PostError.UN_KNOWN)
            }
        }
    }

    override suspend fun deleteComment(
        postId: Int,
        email: String
    ): Result<Unit, PostError> {
        return withContext(Dispatchers.IO) {
            try {
                val comment = commentController.deleteComment(postId, email)
                if (comment != 1) return@withContext Result.Failure(PostError.NO_CONNECTION)
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(PostError.UN_KNOWN)
            }
        }
    }
}