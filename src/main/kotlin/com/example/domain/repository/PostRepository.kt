package com.example.domain.repository

import com.example.data.dto.CommentDto
import com.example.data.dto.SharePostDto
import com.example.data.util.PostError
import com.example.domain.model.Post
import com.example.domain.util.Result

interface PostRepository {
    suspend fun addPost(post: Post): Result<Unit, PostError>
    suspend fun getAllPosts(email: String): Result<List<Post>, PostError>
    suspend fun updatePost(postId: Int, content: String): Result<Unit, PostError>
    suspend fun deletePost(postId: Int): Result<Unit, PostError>
    suspend fun getPostById(postId: Int, isShared: Boolean): Result<Post, PostError>
    suspend fun updatePostLikes(postId: Int, value: Int, email: String, isShared: Boolean): Result<Unit, PostError>
    suspend fun sharePost(sharePostDto: SharePostDto): Result<Unit, PostError>
    suspend fun updateSharePostContent(
        postId: Int,
        content: String,
        email: String
    ): Result<Unit, PostError>

    suspend fun getMyPosts(
        email: String
    ): Result<List<Post>, PostError>

    suspend fun deleteSharedPost(
        postId: Int,
        email: String
    ): Result<Unit, PostError>

    suspend fun addComment(
        postId: Int, comment: String, isShared: Boolean,
        email: String
    ): Result<Unit, PostError>

    suspend fun updateComment(
        postId: Int,
        comment: String,
        email: String
    ): Result<Unit, PostError>

    suspend fun getCommentsForPost(
        postId: Int
    ): Result<List<CommentDto>, PostError>

    suspend fun deleteComment(
        postId: Int,
        email: String
    ): Result<Unit, PostError>
}