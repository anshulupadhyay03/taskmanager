package com.example.taskmanager

import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.respository.TaskRepository
import com.example.taskmanager.domain.usecase.AddTaskUseCase
import com.example.taskmanager.domain.usecase.DeleteTaskUseCase
import com.example.taskmanager.domain.usecase.GetTasksUseCase
import com.example.taskmanager.domain.usecase.UpdateTaskUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

val fakeAddtask = object:AddTaskUseCase {
    override suspend operator fun invoke(task: Task) {

    }
}

val fakeDeletetask = object:DeleteTaskUseCase {
    override suspend operator fun invoke(task: Task) {

    }
}

val fakeGettask = object:GetTasksUseCase {
    override fun invoke(): Flow<List<Task>> {
        return flowOf(emptyList())
    }
}

val fakeUpdateTask = object:UpdateTaskUseCase {
    override suspend operator fun invoke(task: Task) {

    }

    override suspend operator fun invoke(tasks: List<Task>) {

    }
}