package com.example.data.db.dao

import com.example.data.db.DataBaseConnection
import com.example.data.db.entity.UserEntity
import com.example.data.util.DateBaseError
import com.example.data.util.LoginError
import com.example.domain.model.User
import com.example.domain.util.Result
import org.ktorm.dsl.*
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDate

object UserDao {
    private val db = DataBaseConnection.database
    fun signup(user: User): Result<Unit, DateBaseError> {
        val encryptedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt())
        val insertOp = db.insert(UserEntity) {
            set(UserEntity.email, user.email)
            set(UserEntity.name, user.name)
            set(UserEntity.age, user.age)
            set(UserEntity.dateOfCreate, LocalDate.now())
            set(UserEntity.dateOfBirth, user.dateOfBirth)
            set(UserEntity.gender, user.gender)
            set(UserEntity.password, encryptedPassword)
            set(UserEntity.phoneNumber, user.phoneNumber)
        }
        return when {
            insertOp == 1 -> Result.Success(Unit)
            else -> Result.Failure(DateBaseError.CAN_NOT_INSERT)
        }
    }

    fun login(email: String): Result<User?, LoginError> {
        try {
            val entity = getUserFromDB(email)
            return Result.Success(entity)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.Failure(LoginError.NO_CONNECTION)
        }
    }

    fun emails(): Result<List<String>, LoginError> {
        val users = db.from(UserEntity).select().map {
            it[UserEntity.email]!!
        }
        return Result.Success(users)
    }

    fun getUser(email: String): Result<User, LoginError> {
        val user = getUserFromDB(email) ?: return Result.Failure(LoginError.NO_SUCH_USER)
        return Result.Success(user)
    }

     fun getUserFromDB(email: String): User? {
        return db.from(UserEntity).select().where(UserEntity.email eq email)
            .map {
                val localEmail = it[UserEntity.email]!!
                val localPassword = it[UserEntity.password]!!
                val id = it[UserEntity.id]!!
                val age = it[UserEntity.age]!!
                val gender = it[UserEntity.gender]!!
                val name = it[UserEntity.name]!!
                val phoneNumber = it[UserEntity.phoneNumber]!!
                val dateOfBirth = it[UserEntity.dateOfBirth]!!
                val dateOfCreation = it[UserEntity.dateOfCreate]!!
                User(
                    email = localEmail,
                    password = localPassword,
                    id = id,
                    age = age,
                    gender = gender,
                    name = name,
                    phoneNumber = phoneNumber,
                    dateOfCreate = dateOfCreation,
                    dateOfBirth = dateOfBirth
                )
            }.firstOrNull()
    }
}

