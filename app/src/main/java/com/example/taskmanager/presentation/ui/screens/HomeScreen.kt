import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.presentation.ui.components.BouncingFAB
import com.example.taskmanager.presentation.ui.components.CircularProgressBarContainer
import com.example.taskmanager.presentation.ui.components.EmptyStateUI
import com.example.taskmanager.presentation.ui.components.FilterMenu
import com.example.taskmanager.presentation.ui.components.ShimmerTaskList
import com.example.taskmanager.presentation.ui.components.SortMenu
import com.example.taskmanager.presentation.ui.components.SwipeableTaskItem
import com.example.taskmanager.presentation.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    dataStore: DataStore<Preferences>,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val tasks by viewModel.filteredTasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val completionPercentage by viewModel.completionPercentage.collectAsState()
    val currentSort by viewModel.sortOption.collectAsState()
    val currentFilter by viewModel.filterOption.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    // State for managing the task list
    val taskList = remember { mutableStateListOf<Task>().apply { addAll(tasks) } }

    // Sync taskList with ViewModel's tasks
    LaunchedEffect(tasks) {
        taskList.clear()
        taskList.addAll(tasks)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Manager") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("settings")
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    SortMenu(currentSort, viewModel::setSortOption)
                    FilterMenu(currentFilter, viewModel::setFilterOption)

                }
            )
        },
        floatingActionButton = {
            BouncingFAB { navController.navigate("create") }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp)
        ) {
            when {
                isLoading -> {
                    ShimmerTaskList(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    )
                }

                taskList.isEmpty() -> {
                    EmptyStateUI(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    )
                }

                else -> {
                    val lazyListState = rememberLazyListState()
                    val dragDropState = rememberDragDropState(lazyListState) { from, to ->
                        taskList.apply {
                            add(to, removeAt(from))
                        }
                    }
                    CircularProgressBarContainer(padding, completionPercentage)
                    LazyColumn(
                        modifier = Modifier
                            .padding(
                                top = 4.dp,
                                start = padding.calculateStartPadding(LayoutDirection.Ltr),
                                end = padding.calculateEndPadding(LayoutDirection.Ltr),
                                bottom = padding.calculateBottomPadding()
                            )
                            .dragContainer(dragDropState),
                        state = lazyListState
                    ) {
                        itemsIndexed(
                            items = taskList,
                            key = { _, task -> task.id }
                        ) { index, task ->
                            SwipeableTaskItem(
                                task = task,
                                onClick = { navController.navigate("details/${task.id}") },
                                onDelete = {
                                    val removedTask = taskList[index]
                                    val removedIndex = index
                                    taskList.removeAt(index)

                                    coroutineScope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Task deleted: ${removedTask.title}",
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Long // 10 seconds for testing
                                        )
                                        when (result) {
                                            SnackbarResult.ActionPerformed -> {
                                                taskList.add(removedIndex, removedTask)
                                            }
                                            SnackbarResult.Dismissed -> {
                                                viewModel.deleteTask(removedTask.id)
                                            }
                                        }
                                    }
                                },
                                onComplete = {
                                    val completedTask = taskList[index]
                                    val completedIndex = index
                                    taskList.removeAt(index)

                                    coroutineScope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Task completed: ${completedTask.title}",
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Long // 10 seconds for testing
                                        )
                                        when (result) {
                                            SnackbarResult.ActionPerformed -> {
                                                taskList.add(completedIndex, completedTask)
                                            }
                                            SnackbarResult.Dismissed -> {
                                                viewModel.markTaskAsComplete(completedTask.id)
                                            }
                                        }
                                    }
                                },
                                dragDropState = dragDropState,
                                index = index
                            )
                        }
                    }
                }
            }
        }
    }
}