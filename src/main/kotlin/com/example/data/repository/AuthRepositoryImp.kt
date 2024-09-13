package com.example.data.repository

import com.example.data.db.dao.UserDao
import com.example.data.util.DateBaseError
import com.example.data.util.LoginError
import com.example.domain.model.User
import com.example.domain.repository.AuthRepository
import com.example.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt

class AuthRepositoryImp : AuthRepository {
    private val userDao = UserDao
    override suspend fun signUp(user: User): Result<Unit, DateBaseError> {
        return withContext(Dispatchers.IO) {
            userDao.signup(user)
        }
    }

    override suspend fun login(email: String, password: String): Result<User, LoginError> {
        return withContext(Dispatchers.IO) {
            when (val res = userDao.login(email)) {
                is Result.Failure -> Result.Failure(res.error)
                is Result.Success -> {
                    val entity = res.data ?: return@withContext Result.Failure(LoginError.NO_SUCH_USER)
                    val passCheck = BCrypt.checkpw(password, entity.password)
                    println(BCrypt.hashpw("Mohmed@19",BCrypt.gensalt()))
                    if (passCheck) {
                        Result.Success(entity)
                    } else {
                        Result.Failure(LoginError.PASSWORD_IS_WRONG)
                    }
                }
            }
        }
    }

    override suspend fun getAllEmails(): Result<List<String>, LoginError> {
        return withContext(Dispatchers.IO) {
            try {
                when (val users = userDao.emails()) {
                    is Result.Failure -> {
                        return@withContext Result.Failure(LoginError.NO_CONNECTION)
                    }

                    is Result.Success -> {
                        return@withContext Result.Success(users.data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(LoginError.NO_CONNECTION)
            }
        }
    }

    override suspend fun getUserByEmail(email: String): Result<User, LoginError> {
        return withContext(Dispatchers.IO) {
            try {
                when (val res = userDao.getUser(email)) {
                    is Result.Failure -> {
                        return@withContext Result.Failure(res.error)
                    }

                    is Result.Success -> {
                        return@withContext Result.Success(res.data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(LoginError.NO_CONNECTION)
            }
        }
    }
}