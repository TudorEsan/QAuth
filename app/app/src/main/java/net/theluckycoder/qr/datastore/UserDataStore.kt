package net.theluckycoder.qr.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.theluckycoder.qr.model.Tokens
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataStore @Inject constructor(@ApplicationContext context: Context) {

    private val userDataStore = context.userDataStore

    val firstStart: Flow<Boolean> =
        userDataStore.data.map { it[FIRST_START] ?: true }

    suspend fun setFirstStart() = userDataStore.edit { preferences ->
        preferences[FIRST_START] = false
    }

    val tokens: Flow<Tokens?> =
        userDataStore.data.map { Json.decodeFromString<Tokens>(it[TOKENS] ?: return@map null) }
            .distinctUntilChanged()

    suspend fun setTokens(value: Tokens?) = userDataStore.edit { preferences ->
        if (value == null) {
            preferences.clear()
        } else {
            preferences[TOKENS] = Json.encodeToString(value)
        }
    }

    val userIdFlow: Flow<Int> =
        userDataStore.data.map { it[USER_ID] ?: -1 }.distinctUntilChanged()

    suspend fun setUserId(value: Int) = userDataStore.edit { preferences ->
        preferences[USER_ID] = value
    }

    val displayNameFlow: Flow<String> =
        userDataStore.data.map { it[DISPLAY_NAME] ?: "" }.distinctUntilChanged()

    suspend fun setDisplayName(value: String) = userDataStore.edit { preferences ->
        preferences[DISPLAY_NAME] = value
    }

    private companion object {
        private val Context.userDataStore by preferencesDataStore("user_prefs")

        private const val KEY_CREDENTIALS = "credentials"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_DISPLAY_NAME = "display_name"

        private val FIRST_START = booleanPreferencesKey("first_start")
        private val TOKENS = stringPreferencesKey(KEY_CREDENTIALS)
        private val USER_ID = intPreferencesKey(KEY_USER_ID)
        private val DISPLAY_NAME = stringPreferencesKey(KEY_DISPLAY_NAME)
    }
}
