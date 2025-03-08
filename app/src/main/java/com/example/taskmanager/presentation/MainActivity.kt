package com.example.taskmanager.presentation

import HomeScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskmanager.presentation.ui.screens.SettingsScreen
import com.example.taskmanager.presentation.ui.screens.TaskCreationScreen
import com.example.taskmanager.presentation.ui.screens.TaskDetailScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme(dataStore = dataStore) {
                TaskManagerApp(dataStore = dataStore)
            }
        }
    }
}

@Composable
fun TaskManagerApp(dataStore: DataStore<Preferences>) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController, dataStore) }
        composable("create") { TaskCreationScreen(navController) }
        composable("details/{taskId}") { backStackEntry ->
            TaskDetailScreen(
                navController,
                backStackEntry.arguments?.getString("taskId")?.toInt() ?: 0
            )
        }
        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.navigateUp() },
                dataStore = dataStore
            )
        }
    }
}