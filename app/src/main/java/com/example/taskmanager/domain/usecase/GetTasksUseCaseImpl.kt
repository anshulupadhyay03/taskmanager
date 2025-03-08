package com.example.taskmanager.domain.usecase

import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.respository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTasksUseCaseImpl(private val repository: TaskRepository): GetTasksUseCase {
    override operator fun invoke(): Flow<List<Task>> = repository.getAllTasks()
}