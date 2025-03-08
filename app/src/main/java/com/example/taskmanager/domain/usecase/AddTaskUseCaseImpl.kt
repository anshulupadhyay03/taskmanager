package com.example.taskmanager.domain.usecase

import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.respository.TaskRepository

class AddTaskUseCaseImpl(private val repository: TaskRepository) :AddTaskUseCase{
    override suspend operator fun invoke(task: Task) {
        repository.addTask(task)
    }
}