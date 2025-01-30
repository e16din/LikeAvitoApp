package me.likeavitoapp.model

import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.appBackend
import me.likeavitoapp.appModel
import me.likeavitoapp.appPlatform


class DataSources(
    val app: AppModel,
    val platform: IAppPlatform,
    val backend: AppBackend
)

class DataSourcesWithScreen<T: IScreen>(
    val app: AppModel,
    val platform: IAppPlatform,
    val backend: AppBackend,
    val screen: T
)

class Loadable<T>(initial: T) {
    var data = MutableStateFlow(initial)
    var loading = MutableStateFlow(false)
    var loadingFailed = MutableStateFlow(false)
}

fun dataSources() = DataSources(
    app = appModel,
    platform = appPlatform,
    backend = appBackend,
)

inline fun <reified T : IScreen> dataSourcesWithScreen() = DataSourcesWithScreen(
    app = appModel,
    platform = appPlatform,
    backend = appBackend,
    screen = appModel.currentScreen.value as T
)