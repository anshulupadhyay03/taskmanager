package com.example.taskmanager

import androidx.activity.ComponentActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskmanager.domain.model.Priority
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.presentation.ui.screens.TaskDetailScreen
import com.example.taskmanager.presentation.viewmodel.TaskViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import java.text.DateFormat
import java.util.*

@HiltAndroidTest
class TaskDetailScreenTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var viewModel: TaskViewModel
    private val taskId = 1
    private val dummyTask = Task(
        id = taskId,
        title = "Test Task",
        description = "Test Description",
        priority = Priority.MEDIUM,
        dueDate = Calendar.getInstance().timeInMillis,
        isCompleted = false
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // Mock TaskViewModel
        viewModel = TaskViewModel(
            fakeAddtask, fakeGettask, fakeDeletetask, fakeUpdateTask)

        composeTestRule.setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { Text("Home") }
                composable("details/{taskId}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("taskId")?.toInt() ?: 0
                    TaskDetailScreen(
                        navController = navController,
                        taskId = id,
                        viewModel = viewModel
                    )
                }
            }
            navController.navigate("details/$taskId")
        }
    }

    @Test
    fun testLoadingState() = runTest {
        // Update viewModel to emit loading state
        whenever(viewModel.isLoading).thenReturn(MutableStateFlow(true))
        whenever(viewModel.getTaskById(taskId)).thenReturn(MutableStateFlow(null))

        composeTestRule.waitForIdle()

        // Identify CircularProgressIndicator by its role (progress indicator)
        composeTestRule.onNodeWithText("Test Task").assertDoesNotExist()
    }

    @Test
    fun testTaskFound() = runTest {
        // Verify task details
        composeTestRule.onNodeWithText("Test Task").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Description").assertIsDisplayed()
        composeTestRule.onNodeWithText("MEDIUM").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "Due: ${DateFormat.getDateInstance().format(Date(dummyTask.dueDate))}",
            substring = true
        ).assertIsDisplayed()

        // Test Mark Complete button
        composeTestRule.onNode(
            hasText("Mark Complete") and hasClickAction()
        ).performClick()
        verify(viewModel).markTaskAsComplete(taskId)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Test Task").assertDoesNotExist()
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()

        // Reset for Delete test
        setup() // Re-run setup to reset navigation
        composeTestRule.onNode(
            hasText("Delete") and hasClickAction()
        ).performClick()
        verify(viewModel).deleteTask(taskId)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Test Task").assertDoesNotExist()
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }

    @Test
    fun testTaskNotFound() = runTest {
        // Update viewModel to emit no task
        whenever(viewModel.getTaskById(taskId)).thenReturn(MutableStateFlow(null))
        whenever(viewModel.isLoading).thenReturn(MutableStateFlow(false))

        composeTestRule.waitForIdle()

        // Assuming EmptyStateUI has some identifiable text (e.g., "No tasks" or similar)
        composeTestRule.onNodeWithText("No tasks", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Task").assertDoesNotExist()
    }
}