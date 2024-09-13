package com.example.data.db.entity

import org.ktorm.schema.*

object UserEntity : Table<Nothing>("user") {
    val id = int("id").primaryKey()
    val name = varchar("name")
    val email = varchar("email")
    val password = varchar("password")
    val age = float("age")
    val phoneNumber = varchar("phoneNumber")
    val gender = varchar("gender")
    val dateOfBirth = date("dataOfBirth")
    val dateOfCreate = date("dateOfCreate")
}