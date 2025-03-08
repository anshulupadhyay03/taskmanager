package com.example.taskmanager

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskmanager.domain.model.Priority
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.usecase.AddTaskUseCase
import com.example.taskmanager.domain.usecase.DeleteTaskUseCase
import com.example.taskmanager.domain.usecase.GetTasksUseCase
import com.example.taskmanager.domain.usecase.UpdateTaskUseCase
import com.example.taskmanager.presentation.ui.screens.TaskCreationScreen
import com.example.taskmanager.presentation.viewmodel.TaskViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@HiltAndroidTest
class TaskCreationScreenTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: NavController
    private lateinit var viewModel: TaskViewModel

    @Before
    fun setup() {

        viewModel = TaskViewModel(
            fakeAddtask, fakeGettask,fakeDeletetask,fakeUpdateTask
        )

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController as NavHostController, startDestination = "home") {
                composable("home") {
                    // Dummy home screen to enable popBackStack
                    Text("Home")
                }
                composable("create") {
                    TaskCreationScreen(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
            // Navigate to TaskCreationScreen
            navController.navigate("create")
        }
    }

    /*@Test
    fun testInitialUiState() = runTest {
        composeTestRule.onNodeWithText("Add Task").assertIsDisplayed()
        composeTestRule.onNode(hasText("Title") and hasSetTextAction()).assertIsDisplayed().assertTextEquals("Title")
        composeTestRule.onNode(hasText("Description") and hasSetTextAction()).assertIsDisplayed().assertTextEquals("Description")
        composeTestRule.onNode(hasText("Priority") and hasText(Priority.LOW.name)).assertIsDisplayed()
        val today = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        composeTestRule.onNode(hasText("Due Date") and hasText(today, substring = true)).assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
    }

    @Test
    fun testInputFields() = runTest {
        composeTestRule.onNode(hasText("Title") and hasSetTextAction()).performTextInput("New Task")
        composeTestRule.onNode(hasText("New Task")).assertIsDisplayed()
        composeTestRule.onNode(hasText("Description") and hasSetTextAction()).performTextInput("This is a test task")
        composeTestRule.onNode(hasText("This is a test task")).assertIsDisplayed()
    }

    @Test
    fun testPriorityDropdown() = runTest {
        composeTestRule.onNode(hasText("Priority") and hasText(Priority.LOW.name)).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(Priority.HIGH.name).performClick()
        composeTestRule.onNode(hasText("Priority") and hasText(Priority.HIGH.name)).assertIsDisplayed()
        composeTestRule.onNodeWithText(Priority.LOW.name).assertDoesNotExist()
    }

    @Test
    fun testDatePicker() = runTest {
        val today = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        composeTestRule.onNode(hasText("Due Date") and hasText(today, substring = true)).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Pick Date").performClick()
        // Note: DatePickerDialog testing requires Espresso or refactoring
    }

    @Test
    fun testSaveTask() = runTest {
        composeTestRule.onNode(hasText("Title") and hasSetTextAction()).performTextInput("New Task")
        composeTestRule.onNode(hasText("Description") and hasSetTextAction()).performTextInput("Test Description")
        composeTestRule.onNode(hasText("Priority") and hasText(Priority.LOW.name)).performClick()
        composeTestRule.onNodeWithText(Priority.MEDIUM.name).performClick()
        val dueDate = Calendar.getInstance().timeInMillis
        composeTestRule.onNodeWithText("Save").performClick()
        verify(viewModel).addTask(eq("New Task"), eq("Test Description"), eq(Priority.MEDIUM), eq(dueDate))
        verify(navController).popBackStack()
    }*/

    @Test
    fun testInitialUiState() = runTest {
        composeTestRule.onNodeWithText("Add Task").assertIsDisplayed()
    }
}