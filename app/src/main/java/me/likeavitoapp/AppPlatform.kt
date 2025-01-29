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
import me.likeavitoapp.AppPlatform.Companion.platform
import me.likeavitoapp.screens.auth.AuthScreen

class AuthFiledException : Exception()

fun dataSources() = DataSources(
    app = app,
    platform = platform,
    backend = backend,
)

inline fun <reified T : Screen> dataSourcesWithScreen() = DataSourcesWithScreen(
    app = app,
    platform = platform,
    backend = backend,
    screen = app.currentScreen.value as T
)

val app = AppModel()
val backend = Backend()

@Composable
fun actualScope() = rememberCoroutineScope { platform.defaultContext }

class AppPlatform : Application() {

    companion object {
        lateinit var platform: AppPlatform
    }

    init {
        platform = this
    }

    @OptIn(DelicateCoroutinesApi::class)
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("Error!")
        println(throwable.message)
        if (throwable is AuthFiledException) {
            if (app.currentScreen.value !is AuthScreen) {
                GlobalScope.launch {
                    app.Logout()
                }
            }

        } else {
            throwable.printStackTrace()
        }
    }
    val defaultContext = Dispatchers.Default + Job() + exceptionHandler


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

        suspend fun clear() {
            dataStore.edit { settings ->
                settings.clear()
            }
        }
    }
}