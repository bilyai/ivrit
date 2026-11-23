package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.ivritPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "ivrit_pro_prefs"
)

data class UserPreferences(
    val speechRate: Float = 0.85f,
    val showTransliteration: Boolean = true,
)

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val SPEECH_RATE = floatPreferencesKey("tts_speech_rate")
        val SHOW_TRANSLITERATION = booleanPreferencesKey("show_transliteration")
    }

    val preferencesFlow: Flow<UserPreferences> = context.ivritPreferencesDataStore.data.map { prefs ->
        UserPreferences(
            speechRate = (prefs[Keys.SPEECH_RATE] ?: 0.85f).coerceIn(0.6f, 1.25f),
            showTransliteration = prefs[Keys.SHOW_TRANSLITERATION] ?: true,
        )
    }

    suspend fun setSpeechRate(rate: Float) {
        context.ivritPreferencesDataStore.edit { prefs ->
            prefs[Keys.SPEECH_RATE] = rate.coerceIn(0.6f, 1.25f)
        }
    }

    suspend fun setShowTransliteration(show: Boolean) {
        context.ivritPreferencesDataStore.edit { prefs ->
            prefs[Keys.SHOW_TRANSLITERATION] = show
        }
    }
}
