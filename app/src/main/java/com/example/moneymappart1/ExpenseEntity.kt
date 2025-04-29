package com.example.moneymappart1

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val expenseId: Int = 0,
    val date: String,
    val startTime: String,
    val endTime: String,
    val description: String,
    val category: String,
    val amount: Double,
    val photoUri: String? = null
)
