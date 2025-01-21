package me.likeavitoapp

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.first

class AppPlatform : Application() {

    companion object {
        lateinit var get: AppPlatform
    }

    init {
        get = this
    }

    var backend = Backend(
        HttpClient()
    )
    var userIdStore = UserIdStore()

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    inner class UserIdStore {
        private val USER_ID_KEY = longPreferencesKey("user_id")

        suspend fun load(): Long? {
            val prefs = this@AppPlatform.dataStore.data.first()
            return prefs[USER_ID_KEY]
        }

        suspend fun save(userId: Long) {
            this@AppPlatform.dataStore.edit { settings ->
                settings[USER_ID_KEY] = userId
            }
        }
    }
}