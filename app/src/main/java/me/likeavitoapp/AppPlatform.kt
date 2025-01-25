package me.likeavitoapp

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first

private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    throwable.printStackTrace()
}
val defaultContext = Dispatchers.Default + Job() + exceptionHandler

class AppPlatform : Application() {

    companion object {
        lateinit var get: AppPlatform
    }

    init {
        get = this
    }
    
    val app = AppModel()

    val backend = Backend(
        HttpClient()
    )
    
    val authDataStore = AuthDataStore()

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    inner class AuthDataStore() {
        private val USER_ID_KEY = longPreferencesKey("user_id")
        private val TOKEN_KEY = stringPreferencesKey("token")

        suspend fun loadId(): Long? {
            val prefs = dataStore.data.first()
            return prefs[USER_ID_KEY]
        }

        suspend fun saveId(userId: Long) {
            dataStore.edit { settings ->
                settings[USER_ID_KEY] = userId
            }
        }

        suspend fun loadToken(): String? {
            val prefs = dataStore.data.first()
            return prefs[TOKEN_KEY]
        }

        suspend fun saveToken(token: String) {
            dataStore.edit { settings ->
                settings[TOKEN_KEY] = token
            }
        }
    }
}