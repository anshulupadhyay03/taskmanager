package com.example.taskmanager.domain.usecase

import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.respository.TaskRepository

open class UpdateTaskUseCaseImpl(private val repository: TaskRepository): UpdateTaskUseCase{
    override suspend operator fun invoke(task: Task) {
        repository.updateTask(task)
    }

    override suspend operator fun invoke(tasks: List<Task>) {
        repository.updateTasks(tasks)
    }
}