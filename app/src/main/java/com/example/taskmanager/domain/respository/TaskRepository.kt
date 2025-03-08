package com.example.taskmanager.domain.respository

import com.example.taskmanager.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun addTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun updateTasks(tasks: List<Task>)
    suspend fun deleteTask(task: Task)
    fun getAllTasks(): Flow<List<Task>>
}