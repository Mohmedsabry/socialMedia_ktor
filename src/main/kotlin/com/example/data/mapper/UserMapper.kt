package com.example.data.mapper

import com.example.data.dto.RegisterDto
import com.example.data.dto.UserDto
import com.example.domain.model.User
import java.time.LocalDate
import kotlin.math.max
import kotlin.math.min

fun RegisterDto.toUser() = User(
    id = 0,
    name = name,
    email = email,
    password = password,
    age = LocalDate.parse(dateOfBirth).toAge(),
    phoneNumber = phoneNumber,
    gender = gender,
    dateOfBirth = LocalDate.parse(dateOfBirth),
    dateOfCreate = LocalDate.now()
)

fun User.toDto(): UserDto = UserDto(
    name,
    email,
    password,
    age,
    phoneNumber,
    gender,
    dateOfBirth.toString(),
    dateOfCreate.toString()
)

fun LocalDate.toAge(localDate: LocalDate = LocalDate.now()): Float {
    return if (this.month <= localDate.month) {
        (localDate.year - this.year) + ((max(this.month.value, localDate.month.value) - min(
            this.month.value,
            localDate.month.value
        )) / 12.0f)
    } else {
        (localDate.year - this.year) + ((12 - (max(this.month.value, localDate.month.value) - min(
            this.month.value,
            localDate.month.value
        ))) / 12.0f) - 1
    }
}