package me.likeavitoapp.model

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.AppPlatform
import me.likeavitoapp.defaultContext


class Listener<T>(var value: T) {
    var onCall: ((value: T) -> Unit)? = null

    fun listen(onCall: (value: T) -> Unit) {
        this.onCall = onCall
    }

    fun call(value: T) {
        this@Listener.value = value
        onCall?.invoke(value)
    }
}

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
    var data = Listener<T>(initial)
    var loading = mutableStateOf(false)
    var loadingFailed = mutableStateOf(false)
}


fun mockDataSource() = DataSources(
    app = AppModel(),
    platform = AppPlatform(),
    backend = AppBackend(),
)
fun mockCoroutineScope() = CoroutineScope(defaultContext)
fun mockScreensNavigator() = ScreensNavigator()