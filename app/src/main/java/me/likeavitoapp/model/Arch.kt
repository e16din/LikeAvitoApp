package me.likeavitoapp.model

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.AppPlatform
import me.likeavitoapp.defaultContext


class StateValue<T>(var value: T) {
    var callbacks = mutableMapOf<Any, (value: T) -> Unit>()

    fun listen(key: Any, onChanged: (value: T) -> Unit) {
        callbacks[key] = (onChanged)
    }

    fun set(value: T) {
        this.value = value
        callbacks.values.forEach {
            it.invoke(value)
        }
    }

    fun free(key: Any) {
        callbacks.remove(key)
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

class DataSourcesWithScreen<T : IScreen>(
    val app: AppModel,
    val platform: IAppPlatform,
    val backend: AppBackend,
    val screen: T
)

class Loadable<T>(initial: T) {
    var data = StateValue<T>(initial)
    var loading = StateValue(false)
    var loadingFailed = StateValue(false)
}


fun mockDataSource() = DataSources(
    app = AppModel(),
    platform = AppPlatform(),
    backend = AppBackend(),
)

fun mockCoroutineScope() = CoroutineScope(defaultContext)
fun mockScreensNavigator() = ScreensNavigator()