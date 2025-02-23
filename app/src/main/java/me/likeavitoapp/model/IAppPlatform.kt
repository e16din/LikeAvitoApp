package me.likeavitoapp.model

interface IAppPlatform {
    val appDataStore: IAppDataStore

    interface IAppDataStore {
        suspend fun saveUserId(id: Long)
        suspend fun loadUserId(): Long?

        suspend fun saveToken(token: String)
        suspend fun loadToken(): String?

        suspend fun saveCategoryId(id: Int)
        suspend fun loadCategoryId(): Int?

        suspend fun clear()
    }
}