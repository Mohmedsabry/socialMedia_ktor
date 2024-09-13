package com.example.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterDto(
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val gender: String,
    val dateOfBirth: String
)
