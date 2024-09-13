package com.example.domain.model

import java.time.LocalDate

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val age: Float,
    val phoneNumber: String,
    val gender: String,
    val dateOfBirth: LocalDate,
    val dateOfCreate: LocalDate,
)
