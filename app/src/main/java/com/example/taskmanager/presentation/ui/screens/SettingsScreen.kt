package com.example.taskmanager.presentation.ui.screens

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.taskmanager.domain.respository.AppPreferencesModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    dataStore: DataStore<Preferences>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val primaryColorHex by AppPreferencesModule.providePrimaryColorFlow(dataStore)
        .collectAsState(initial = "#6200EE")
    val themeMode by AppPreferencesModule.provideThemeModeFlow(dataStore)
        .collectAsState(initial = "SYSTEM")
    var tempPrimaryColor by remember(primaryColorHex) { mutableStateOf(primaryColorHex) }

    val isDarkTheme = when (themeMode) {
        "DARK" -> true
        "LIGHT" -> false
        else -> isSystemInDarkTheme()
    }

    val customPrimaryColor = try {
        Color(android.graphics.Color.parseColor(tempPrimaryColor))
    } catch (e: Exception) {
        Color(0xFF6200EE)
    }

    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val dynamicScheme = if (isDarkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
            dynamicScheme.copy(primary = customPrimaryColor)
        }
        isDarkTheme -> darkColorScheme(primary = customPrimaryColor)
        else -> lightColorScheme(primary = customPrimaryColor)
    }

    MaterialTheme(colorScheme = colorScheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Dark Mode",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { isChecked ->
                            CoroutineScope(Dispatchers.IO).launch {
                                AppPreferencesModule.saveThemeMode(
                                    dataStore,
                                    if (isChecked) "DARK" else "LIGHT"
                                )
                            }
                        },
                        enabled = true
                    )
                }
                Text(
                    text = if (themeMode == "SYSTEM") "Follows system theme" else "Manual override",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = tempPrimaryColor,
                    onValueChange = { newValue ->
                        if (newValue.startsWith("#") && newValue.length <= 7) {
                            tempPrimaryColor = newValue
                        } else if (newValue.isEmpty()) {
                            tempPrimaryColor = "#"
                        }
                    },
                    label = { Text("Primary Color (e.g., #FF5722)") },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(customPrimaryColor)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            AppPreferencesModule.savePrimaryColor(dataStore, tempPrimaryColor)
                            // Ensure the navigation happens after the save is complete
                            withContext(Dispatchers.Main) {
                                onNavigateBack()
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save")
                }
            }
        }
    }
}