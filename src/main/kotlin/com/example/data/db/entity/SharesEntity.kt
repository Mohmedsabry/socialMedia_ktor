package com.example.data.db.entity

import org.ktorm.schema.*

object SharesEntity : Table<Nothing>("shares") {
    val postId = int("post_id")

    // original user email that shares post
    val email = varchar("email")
    val content = text("content")
    val dateOfShare = date("dateOfShare")
    val id = int("id").primaryKey()
    val likes = int("likes")
    val shares = int("shares")
    val comments = int("comments")
    val type = varchar("type")
}