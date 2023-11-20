package com.roman.lab1.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE username IN (:usernameToFind)")
    fun findByUsername(usernameToFind: String): Flow<User>

    @Insert
    suspend fun insertAll(vararg users: User)
}