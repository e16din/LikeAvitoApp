package me.likeavitoapp.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.likeavitoapp.developer.primitives.debug
import me.likeavitoapp.developer.primitives.work
import me.likeavitoapp.get
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass


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

    @Deprecated("Use next()")
    fun post(value: T, scope: CoroutineScope = get.scope(), ifNew: Boolean = false) {
        if (!ifNew || (ifNew && _value != value)) {
            debug {
                println("post: $value")
            }
            scope.launch(get.defaultContext + Dispatchers.Main) {
                _value = value
                callbacks.keys.forEach {
                    callbacks[it]?.forEach { onChange ->
                        onChange(value)
                    }
                }
            }
        }
    }

    fun next(value: T, ifNew: Boolean = false) {
        if (!ifNew || (ifNew && _value != value)) {
            debug {
                println("next: $value")
            }
            _value = value
            callbacks.keys.forEach {
                callbacks[it]?.forEach { onChange ->
                    onChange(value)
                }
            }
        }
    }

    fun repostTo(state: UpdatableState<T>, key: Any = Unit) {
        listen(key) { value ->
            state.next(value)
        }
    }

    fun free(key: Any) {
        callbacks.remove(key)
    }
}

@Composable
fun <T : R, R> UpdatableState<T>.collectAsState(
    key: KClass<*> = Unit::class,
    initial: R = this.value,
    context: CoroutineContext = EmptyCoroutineContext
): State<R> = produceState(initial, this.value) {
    if (context == EmptyCoroutineContext) {
        listen(key) { value = it }

    } else withContext(context) {
        listen(key) { value = it }
    }
}

// NOTE: У приложения всегда есть несколько источников данных.
// как правило это: платформа/OS | сервер/backend | модель приложения
// и они соответствуют реальным источникам: Disk | Удаленный Disk | Оперативная память
class DataSources(
    val app: AppModel,
    val platform: IAppPlatform,
    val backend: AppBackend
)

// NOTE: обратил внимание что интерфейс юнит-тестов
// совпадает с интерфейсом обработки ошибок при загрузке данных
// и получается по сути любая обработка данных сводится к этому интерфейсу.
// Поэтому - Worker который выводит output :)
class Worker<T>(initial: T) {
    var output = UpdatableState<T>(initial)
    var working = UpdatableState(false)
    var fail = UpdatableState(false)

    var isDoOnceCalled = false

    fun resetWith(newData: T) {
        output.post(newData)
        working.post(false, ifNew = true)
        fail.post(false, ifNew = true)
    }

    inline fun worker(doOnce: () -> Unit = {}): Worker<T> {
        if (!isDoOnceCalled) {
            doOnce()
            isDoOnceCalled = true
        }
        return this
    }

    fun data() = output.value
    fun hasFail() = fail.value
}

// NOTE: act - действуй!
// (кандидат run() отпал, слишком заезжено и много переопределений что может вызывать путаницу)
inline fun <T> Worker<T>.act(
    crossinline afterAll: (T) -> Unit = {},
    crossinline task: suspend () -> Pair<T?, Boolean>
) {
    working.next(true)
    work(onDone = { result ->
        val data = result.first ?: output.value
        output.next(data)
        if (result.second) {
            fail.next(false, ifNew = true)

        } else {
            fail.next(true)
        }

        working.next(false)
        afterAll(data)
    }) {
        return@work task()
    }

}