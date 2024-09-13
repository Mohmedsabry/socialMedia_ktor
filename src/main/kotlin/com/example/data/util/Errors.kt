package com.example.data.util

import com.example.util.Error

enum class DateBaseError : Error {
    NO_CONNECTION,
    CAN_NOT_INSERT
}

enum class LoginError(val massage: String) : Error {
    NO_CONNECTION("no connection"),
    NO_SUCH_USER("user is not found"),
    PASSWORD_IS_WRONG("password is not correct")
}

enum class PostError(val massage: String) : Error {
    NO_CONNECTION("no connection"),
    UN_KNOWN("un known error"),
    THERE_IS_NO_DATA("there is no posts for that user")
}
enum class FriendsError(val massage: String) : Error {
    NO_CONNECTION("no connection"),
    UN_KNOWN("un known error"),
    THERE_IS_NO_DATA("there is no posts for that user")
}