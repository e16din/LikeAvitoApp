package me.likeavitoapp.model

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.likeavitoapp.AppPlatform
import me.likeavitoapp.defaultContext
import me.likeavitoapp.provideCoroutineScope


class UpdatableState<T>(initial: T) {
    private var _value: T = initial
    var value: T
        get() = _value
        set(value) {
            throw IllegalStateException("Use '.post($value)' instead")
        }

    var callbacks = mutableMapOf<Any, (value: T) -> Unit>()

    fun listen(key: Any, onChanged: (value: T) -> Unit) {
        callbacks[key] = onChanged
    }

    fun post(value: T) {
        provideCoroutineScope().launch(defaultContext + Dispatchers.Main) {
            _value = value
            callbacks.values.forEach {
                it.invoke(value)
            }
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

class DataSourcesWithScreen<T : BaseScreen>(
    val app: AppModel,
    val platform: IAppPlatform,
    val backend: AppBackend,
    val screen: T
)

class Loadable<T>(initial: T) {
    var data = UpdatableState<T>(initial)
    var loading = UpdatableState(false)
    var loadingFailed = UpdatableState(false)
}

class LoadableState<T>(initial: T) {
    var data = mutableStateOf(initial)
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