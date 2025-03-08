package com.example.taskmanager.data.repository

import com.example.taskmanager.data.db.TaskDao
import com.example.taskmanager.data.db.TaskEntity
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.respository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(private val dao: TaskDao) : TaskRepository {
    override suspend fun addTask(task: Task) {
        dao.insert(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        dao.update(task.toEntity())
    }

    override suspend fun updateTasks(tasks: List<Task>) {
        dao.updateAll(tasks.map { it.toEntity() })
    }

    override suspend fun deleteTask(task: Task) {
        dao.delete(task.toEntity())
    }

    override fun getAllTasks(): Flow<List<Task>> {
        return dao.getAllTasks().map { entities -> entities.map { it.toDomain() } }
    }

    private fun Task.toEntity() = TaskEntity(id, title, description, priority, dueDate, isCompleted)
    private fun TaskEntity.toDomain() = Task(id, title, description, priority, dueDate, isCompleted)
}