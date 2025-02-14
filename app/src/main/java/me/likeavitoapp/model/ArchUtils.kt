package me.likeavitoapp.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.likeavitoapp.defaultContext
import me.likeavitoapp.provideCoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass
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

    fun post(value: T, scope: CoroutineScope = provideCoroutineScope(), ifNew: Boolean = false) {
        if (!ifNew || (ifNew && _value != value)) {
            scope.launch(defaultContext + Dispatchers.Main) {
                _value = value
                callbacks.keys.forEach {
                    callbacks[it]?.forEach { onChange ->
                        onChange(value)
                    }
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

    fun freeAll() {
        callbacks.clear()
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

    fun resetWith(newData: T) {
        output.post(newData)
        working.post(false, ifNew = true)
        fail.post(false, ifNew = true)
    }

   fun worker() = this
   fun data() = output.value
   fun hasFail() = fail.value
}

// NOTE: act - действуй!
// (кандидат run() отпал, слишком заезжено и много переопределений что может вызывать путаницу)
fun <T> Worker<T>.act(task: () -> Result<T>) {
    working.post(true)
    val result = task()
    if (result.isSuccess) {
        fail.post(false, ifNew = true)
        output.post(result.getOrNull()!!)

    } else {
        fail.post(true)
    }
    working.post(false)
}