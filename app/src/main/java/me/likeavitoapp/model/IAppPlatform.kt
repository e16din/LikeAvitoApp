package me.likeavitoapp.model

interface IAppPlatform {
    fun getString(resId: Int): String?
    fun getString(resId: Int, vararg formatArgs:Any?): String?

    val appDataStore: IAppDataStore

    var screenWidthDp: Int
    var screenHeightDp: Int

    interface IAppDataStore {
        suspend fun saveUserId(id: Long)
        suspend fun loadUserId(): Long?

        suspend fun saveToken(token: String)
        suspend fun loadToken(): String?

        suspend fun saveCategoryId(id: Int)
        suspend fun loadCategoryId(): Int?

        suspend fun saveRegionId(id: Int)
        suspend fun loadRegionId(): Int?

        suspend fun clear()
    }
}