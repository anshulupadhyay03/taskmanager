package com.example.taskmanager.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taskmanager.domain.model.Priority

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val priority: Priority,
    val dueDate: Long,
    val isCompleted: Boolean = false
)