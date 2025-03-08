package com.example.taskmanager.domain.model

data class Task(
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.LOW,
    val dueDate: Long,
    val isCompleted: Boolean = false
)

enum class Priority {
    LOW, MEDIUM, HIGH
}