package me.likeavitoapp.model

interface IAppPlatform {
    val appDataStore: IAppDataStore

    interface IAppDataStore {
        suspend fun loadId(): Long?
        suspend fun saveId(userId: Long)
        suspend fun loadToken(): String?
        suspend fun saveToken(token: String)
        suspend fun clear()
    }
}