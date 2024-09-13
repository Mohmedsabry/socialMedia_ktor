package com.example.data.db.entity

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object LikesEntity : Table<Nothing>("likes") {
    val postId = int("postId")
    val email = varchar("email")
}