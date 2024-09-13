package com.example.data.mapper

import com.example.data.db.local.ShareLocalEntity
import com.example.data.dto.PostDto
import com.example.data.dto.SendPost
import com.example.domain.model.Post
import com.example.domain.model.TypeOfPost
import com.example.domain.model.User
import java.time.LocalDate

fun PostDto.toPost(): Post = Post(
    user = User(0, "", editor, "", 0f, "", "", dateOfBirth = LocalDate.now(), LocalDate.now()),
    content = content,
    type = if (type == 0) TypeOfPost.PUBLIC else if (type == 1) TypeOfPost.PRIVATE else TypeOfPost.ONLY_FRIENDS,
    postId = 0,
    likes = 0,
    comments = 0,
    shares = 0,
    isLiked = false,
    sharedContent = ""
)

fun Post.toUserReceive(): SendPost = SendPost(
    postId = postId,
    user = user.toDto(),
    likes = likes,
    comments = comments,
    shares = shares,
    content = content,
    type = type.title,
    isShared = isShared,
    isLiked = this.isLiked,
    sharedContent = sharedContent
)

fun ShareLocalEntity.toPost(user: User, isLiked: Boolean) = Post(
    postId = this.sharedPostId,
    likes = this.likes,
    comments = this.comments,
    shares = this.shares,
    type = if (type == "public") TypeOfPost.PUBLIC else if (type == "private") TypeOfPost.PRIVATE else TypeOfPost.ONLY_FRIENDS,
    content = this.content,
    isShared = true,
    user = user,
    isLiked = isLiked,
    sharedContent = sharedContent
)