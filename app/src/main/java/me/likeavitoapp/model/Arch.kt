package me.likeavitoapp.model

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.likeavitoapp.AppPlatform
import me.likeavitoapp.defaultContext
import me.likeavitoapp.provideCoroutineScope
import kotlin.reflect.KProperty

operator fun <T> UpdatableState<T>.getValue(thisRef: Any?, property: KProperty<*>): T = value

class UpdatableState<T>(initial: T) {

    private var _value: T = initial
    var value: T
        get() = _value
        set(value) {
            throw IllegalStateException("Use '.post($value)' instead")
        }

    private var callbacks = mutableMapOf<Any, List<(value: T) -> Unit>>()

    fun listen(key: Any = Unit, onChanged: (value: T) -> Unit) {
        callbacks[key] = callbacks[key]?.let {
            it + onChanged
        } ?: listOf(onChanged)
    }

    fun post(value: T) {
        provideCoroutineScope().launch(defaultContext + Dispatchers.Main) {
            _value = value
            callbacks.keys.forEach {
                callbacks[it]?.forEach { onChange ->
                    onChange(value)
                }
            }
        }
    }

    fun free(key: Any) {
        callbacks.remove(key)
    }

    fun repostTo(state: UpdatableState<T>, key: Any = Unit) {
        listen(key) { value ->
            state.post(value)
        }
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