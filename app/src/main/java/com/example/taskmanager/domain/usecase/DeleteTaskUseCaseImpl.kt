package com.example.taskmanager.domain.usecase

import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.respository.TaskRepository

class DeleteTaskUseCaseImpl(private val repository: TaskRepository) : DeleteTaskUseCase{
    override suspend operator fun invoke(task: Task) {
        repository.deleteTask(task)
    }
}