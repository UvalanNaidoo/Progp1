package com.example.moneymappart1

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val goalId: Int = 0,
    val minGoal: Double,
    val maxGoal: Double
)
