package com.example.moneymappart1

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val categoryId: Int = 0,
    val name: String
)
