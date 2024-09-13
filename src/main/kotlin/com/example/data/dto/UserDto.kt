package com.example.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val name: String,
    val email: String,
    val password: String,
    val age: Float,
    val phoneNumber: String,
    val gender: String,
    val dateOfBirth: String,
    val dateOfCreate: String,
)