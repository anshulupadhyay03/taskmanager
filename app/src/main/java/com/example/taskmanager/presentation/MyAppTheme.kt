package com.example.taskmanager.presentation

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.taskmanager.domain.respository.AppPreferencesModule

@Composable
fun MyAppTheme(
    dataStore: DataStore<Preferences>,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    // Use a LaunchedEffect to ensure the Flow is collected fresh on recomposition
    val primaryColorHex by AppPreferencesModule.providePrimaryColorFlow(dataStore)
        .collectAsState(initial = "#6200EE")
    val themeMode by AppPreferencesModule.provideThemeModeFlow(dataStore)
        .collectAsState(initial = "SYSTEM")

    val customPrimaryColor = remember(primaryColorHex) {
        try {
            Color(android.graphics.Color.parseColor(primaryColorHex))
        } catch (e: Exception) {
            Color(0xFF6200EE) // Default purple
        }
    }

    val isDarkTheme = when (themeMode) {
        "DARK" -> true
        "LIGHT" -> false
        else -> isSystemInDarkTheme() // "SYSTEM" follows system setting
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

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}