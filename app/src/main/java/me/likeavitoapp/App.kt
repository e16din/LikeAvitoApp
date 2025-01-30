package me.likeavitoapp

import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.likeavitoapp.model.IAppPlatform
import me.likeavitoapp.screens.auth.AuthScreen

class AuthFiledException : Exception()


@OptIn(DelicateCoroutinesApi::class)
private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    println("Error!")
    println(throwable.message)
    if (throwable is AuthFiledException) {
        if (appModel.currentScreen.value !is AuthScreen) {
            GlobalScope.launch {
                appModel.Logout()
            }
        }

    } else {
        throwable.printStackTrace()
    }
}
val defaultContext = Dispatchers.Default + Job() + exceptionHandler

@Composable
fun actualScope() = rememberCoroutineScope { defaultContext }

class App : IAppPlatform, Application() {

    init {
        appPlatform = this
    }

    override val appDataStore = AuthDataStore()

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    inner class AuthDataStore() : IAppPlatform.IAppDataStore {
        private val USER_ID_KEY = longPreferencesKey("user_id")
        private val TOKEN_KEY = stringPreferencesKey("token")

        override suspend fun loadId(): Long? {
            val prefs = dataStore.data.first()
            return prefs[USER_ID_KEY]
        }

        override suspend fun saveId(userId: Long) {
            dataStore.edit { settings ->
                settings[USER_ID_KEY] = userId
            }
        }

        override suspend fun loadToken(): String? {
            val prefs = dataStore.data.first()
            return prefs[TOKEN_KEY]
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