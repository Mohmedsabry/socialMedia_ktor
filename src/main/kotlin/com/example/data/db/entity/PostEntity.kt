package com.example.data.db.entity

import org.ktorm.schema.*

object PostEntity : Table<Nothing>("posts") {
    val postId = int("post_id").primaryKey()
    val editor = varchar("editor")
    val likes = int("likes")
    val comments = int("comments")
    val shared = int("shared")
    val content = text("content")
    val type = varchar("type")
    val dateOfCreate = date("dateOfCreate")
}