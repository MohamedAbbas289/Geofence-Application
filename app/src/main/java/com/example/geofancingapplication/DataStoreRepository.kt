package com.example.geofancingapplication

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.geofancingapplication.util.Constants.PREFERENCE_FIRST_LAUNCH
import com.example.geofancingapplication.util.Constants.PREFERENCE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(PREFERENCE_NAME)

@ViewModelScoped
class DataStoreRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferenceKeys {
        val firstLaunch = booleanPreferencesKey(PREFERENCE_FIRST_LAUNCH)
    }

    private val dataStore = context.dataStore

    suspend fun saveFirstLaunch(firstLaunch: Boolean) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.firstLaunch] = firstLaunch
        }
    }

    val readFirstLaunch: Flow<Boolean> = dataStore.data
        .catch {
            emptyPreferences()
        }
        .map { preference ->
            val firstLaunch = preference[PreferenceKeys.firstLaunch] ?: true
            firstLaunch
        }

}