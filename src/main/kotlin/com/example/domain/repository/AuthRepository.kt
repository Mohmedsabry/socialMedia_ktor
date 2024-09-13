package com.example.domain.repository

import com.example.data.util.DateBaseError
import com.example.data.util.LoginError
import com.example.domain.model.User
import com.example.domain.util.Result

interface AuthRepository {
    suspend fun signUp(user: User): Result<Unit, DateBaseError>
    suspend fun login(
        email: String,
        password: String
    ): Result<User, LoginError>

    suspend fun getAllEmails(): Result<List<String>, LoginError>
    suspend fun getUserByEmail(email: String): Result<User, LoginError>
}