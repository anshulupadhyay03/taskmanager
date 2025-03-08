package com.example.taskmanager.presentation.ui.screens

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.taskmanager.domain.model.Priority
import com.example.taskmanager.presentation.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreationScreen(
    navController: NavController,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.LOW) }
    var expanded by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }
    var dueDate by remember { mutableLongStateOf(calendar.timeInMillis) }
    val formattedDate = remember(dueDate) {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(
            Date(dueDate)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add Task",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 10,
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = priority.name,
                        onValueChange = {},
                        label = { Text("Priority") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(Priority.LOW.name) },
                            onClick = { priority = Priority.LOW; expanded = false })
                        DropdownMenuItem(
                            text = { Text(Priority.MEDIUM.name) },
                            onClick = { priority = Priority.MEDIUM; expanded = false })
                        DropdownMenuItem(
                            text = { Text(Priority.HIGH.name) },
                            onClick = { priority = Priority.HIGH; expanded = false })
                    }
                }

                OutlinedTextField(
                    value = formattedDate,
                    onValueChange = {},
                    label = { Text("Due Date") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = {
                            showDatePicker(context, calendar) {
                                dueDate = it
                            }
                        }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                        }
                    }
                )
            }
        }

        Button(
            onClick = {
                viewModel.addTask(title, description, priority, dueDate)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text("Save", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/**
 * Function to show a DatePicker dialog
 */
fun showDatePicker(context: Context, calendar: Calendar, onDateSelected: (Long) -> Unit) {
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            onDateSelected(calendar.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}
