package com.example.moneymappart1

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val minAmount: Double,
    val maxAmount: Double
)



