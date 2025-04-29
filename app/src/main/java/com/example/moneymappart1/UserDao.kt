package com.example.moneymappart1

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun login(username: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username OR email = :email")
    suspend fun findUserByUsernameOrEmail(username: String, email: String): UserEntity?
}
