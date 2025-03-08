package com.example.taskmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.Priority
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.usecase.AddTaskUseCase
import com.example.taskmanager.domain.usecase.DeleteTaskUseCase
import com.example.taskmanager.domain.usecase.GetTasksUseCase
import com.example.taskmanager.domain.usecase.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class TaskViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _isLoading = MutableStateFlow(true) // New loading state
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _sortOption = MutableStateFlow(SortOption.BY_PRIORITY)
    val sortOption: StateFlow<SortOption> = _sortOption
    private val _filterOption = MutableStateFlow(FilterOption.ALL)
    val filterOption: StateFlow<FilterOption> = _filterOption

    val completionPercentage: StateFlow<Float> = _tasks.map { taskList ->
        if (taskList.isEmpty()) 0f
        else (taskList.count { it.isCompleted }.toFloat() / taskList.size) * 100f
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0f)

    val filteredTasks: StateFlow<List<Task>> =
        combine(_tasks, _sortOption, _filterOption) { taskList, sort, filter ->
            taskList.filter { task ->
                when (filter) {
                    FilterOption.ALL -> true
                    FilterOption.COMPLETED -> task.isCompleted
                    FilterOption.PENDING -> !task.isCompleted
                }
            }.sortedWith(compareByDescending<Task> {
                when (sort) {
                    SortOption.BY_PRIORITY -> it.priority.ordinal * -1 // Higher priority first
                    SortOption.BY_DUE_DATE -> it.dueDate // Earlier due date first
                    SortOption.ALPHABETICALLY -> null // Handle separately
                }
            }.thenBy { if (sort == SortOption.ALPHABETICALLY) it.title.lowercase() else null })
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            _isLoading.value = true // Start loading
            delay(1000)
            getTasksUseCase().collect {
                _tasks.value = it
                _isLoading.value = false
            }
        }
    }

    fun setSortOption(sortOption: SortOption) {
        _sortOption.value = sortOption
    }

    fun setFilterOption(filterOption: FilterOption) {
        _filterOption.value = filterOption
    }

    fun addTask(title: String, description: String, priority: Priority, dueDate: Long) {
        viewModelScope.launch {
            val task = Task(
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate
            )
            addTaskUseCase(task)
        }
    }

    fun markTaskAsComplete(taskId: Int) {
        viewModelScope.launch {
            val task = _tasks.value.find { it.id == taskId } ?: return@launch
            updateTaskUseCase(task.copy(isCompleted = true))
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            val task = _tasks.value.find { it.id == taskId } ?: return@launch
            deleteTaskUseCase(task)
        }
    }

    fun getTaskById(taskId: Int): StateFlow<Task?> {
        return _tasks.map { taskList ->
            taskList.find { it.id == taskId }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

}

enum class SortOption { BY_PRIORITY, BY_DUE_DATE, ALPHABETICALLY }
enum class FilterOption { ALL, COMPLETED, PENDING }