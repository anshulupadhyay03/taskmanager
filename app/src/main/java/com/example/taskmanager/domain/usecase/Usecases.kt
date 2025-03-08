package com.example.taskmanager.domain.usecase

import com.example.taskmanager.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface AddTaskUseCase {
    suspend operator fun invoke(task: Task)
}

interface GetTasksUseCase {
    operator fun invoke(): Flow<List<Task>>
}

interface DeleteTaskUseCase {
    suspend operator fun invoke(task: Task)
}

interface UpdateTaskUseCase {
    suspend operator fun invoke(task: Task)
    suspend operator fun invoke(tasks: List<Task>)
}