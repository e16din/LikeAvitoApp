package me.likeavitoapp


@Suppress("UNCHECKED_CAST")
class  DataSources< T: Screen>(
    val app: AppModel = AppModel,
    val screen: T = app.screens.last() as T,
    val platform: AppPlatform = AppPlatform.get,
    val backend: Backend = AppPlatform.get.backend
)