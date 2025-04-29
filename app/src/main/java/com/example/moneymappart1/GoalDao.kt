package com.example.moneymappart1

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GoalDao {
    @Insert
    suspend fun insertGoal(goal: GoalEntity)

    @Query("SELECT * FROM goals LIMIT 1")
    suspend fun getGoal(): GoalEntity?
}
