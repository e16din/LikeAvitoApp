package me.likeavitoapp.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.AppPlatform
import me.likeavitoapp.defaultContext


class DataSources(
    val app: AppModel,
    val platform: IAppPlatform,
    val backend: AppBackend
)

class ScreenArguments(
    val scope: CoroutineScope,
    val parentNavigator: ScreensNavigator,
    val sources: DataSources
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

fun mockDataSource() = DataSources(
    app = AppModel(),
    platform = AppPlatform(),
    backend = AppBackend(),
)
fun mockCoroutineScope() = CoroutineScope(defaultContext)
fun mockScreensNavigator() = ScreensNavigator()