package me.likeavitoapp

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import me.likeavitoapp.model.IAppPlatform

class UnauthorizedException : Exception("UnauthorizedException")



class AppPlatform : IAppPlatform, Application() {

    override val appDataStore = AuthDataStore()

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    inner class AuthDataStore() : IAppPlatform.IAppDataStore {
        private val USER_ID_KEY = longPreferencesKey("user_id")
        private val CATEGORY_ID_KEY = intPreferencesKey("category_id")
        private val TOKEN_KEY = stringPreferencesKey("token")

        override suspend fun loadUserId(): Long? {
            val prefs = dataStore.data.first()
            return prefs[USER_ID_KEY]
        }

        override suspend fun saveUserId(id: Long) {
            dataStore.edit { settings ->
                settings[USER_ID_KEY] = id
            }
        }

        override suspend fun loadToken(): String? {
            val prefs = dataStore.data.first()
            return prefs[TOKEN_KEY]
        }

        override suspend fun saveCategoryId(id: Int) {
            dataStore.edit { settings ->
                settings[CATEGORY_ID_KEY] = id
            }
        }

        override suspend fun loadCategoryId(): Int? {
            val prefs = dataStore.data.first()
            return prefs[CATEGORY_ID_KEY]
        }

        override suspend fun saveToken(token: String) {
            dataStore.edit { settings ->
                settings[TOKEN_KEY] = token
            }
        }

        override suspend fun clear() {
            dataStore.edit { settings ->
                settings.clear()
            }
        }
    }
}