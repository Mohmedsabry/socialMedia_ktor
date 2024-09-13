package com.example.data.db.entity

import org.ktorm.schema.*

object CommentsEntity : Table<Nothing>("comments") {
    val postId = int("post_id")
    val email = varchar("email")
    val comment = text("comment")
    val dateOfComment = date("dateOfComment")
}