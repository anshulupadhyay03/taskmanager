package com.example.taskmanager.domain.respository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppPreferencesModule {

    private val PRIMARY_COLOR_KEY = stringPreferencesKey("primary_color")
    private val THEME_MODE_KEY = stringPreferencesKey("theme_mode") // "LIGHT", "DARK", "SYSTEM"

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { File(context.filesDir, "datastore/settings.preferences_pb") }
        )
    }

    @Provides
    fun providePrimaryColorFlow(dataStore: DataStore<Preferences>): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[PRIMARY_COLOR_KEY] ?: "#6200EE" // Default purple
        }
    }

    @Provides
    fun provideThemeModeFlow(dataStore: DataStore<Preferences>): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[THEME_MODE_KEY] ?: "SYSTEM" // Default to system theme
        }
    }

    suspend fun savePrimaryColor(dataStore: DataStore<Preferences>, colorHex: String) {
        dataStore.edit { preferences ->
            preferences[PRIMARY_COLOR_KEY] = colorHex
        }
    }

    suspend fun saveThemeMode(dataStore: DataStore<Preferences>, mode: String) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode
        }
    }
}