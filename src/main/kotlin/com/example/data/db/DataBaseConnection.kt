package com.example.data.db

import org.ktorm.database.Database

object DataBaseConnection {
    val database = Database.connect(
        url = "jdbc:mysql://localhost:3306/social_media",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "root"
    )
}