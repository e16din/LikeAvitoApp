package me.likeavitoapp


interface UserDataSource

class  DataSources<T: UserDataSource>(
    val user: T,
    val app: AppModel = AppModel,
    val platform: AppPlatform = AppPlatform.get,
    val backend: Backend = AppPlatform.get.backend
)