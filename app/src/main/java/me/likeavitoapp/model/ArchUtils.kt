package me.likeavitoapp.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.likeavitoapp.R
import me.likeavitoapp.className
import me.likeavitoapp.developer.primitives.debug
import me.likeavitoapp.developer.primitives.work
import me.likeavitoapp.get
import me.likeavitoapp.log
import kotlin.reflect.KClass


class UpdatableState<T>(initial: T) {

    private var _value: T = initial
    var value: T
        get() = _value
        set(value) {
            throw IllegalStateException("Use '.next($value)' instead")
        }

    private var callbacks = mutableMapOf<Any, List<(value: T) -> Unit>>()

    fun listen(key: Any = Unit, onChanged: (value: T) -> Unit) {
        callbacks[key] = callbacks[key]?.let {
            it + onChanged
        } ?: listOf(onChanged)
    }

    @Deprecated("Use next()")
    fun post(value: T, scope: CoroutineScope = get.scope(), ifNew: Boolean = false) {
        if (!ifNew || (_value != value)) {
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
        if (!ifNew || (_value != value)) {
//            debug {
//                println("next: $value")
//            }
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
    initial: R = this.value
): State<R> = produceState(initial, this.value) {
    listen(key) {
        this@produceState.value = it
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
        output.next(newData)
        working.next(false, ifNew = true)
        fail.next(false, ifNew = true)
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

inline fun <T> Worker<T>.load(
    crossinline onDone: (T) -> Unit = {},
    crossinline task: suspend () -> Pair<T?, Boolean>
) {
    working.repostTo(get.sources().app.loading)
    fail.listen { failed ->
        if (failed) {
            get.sources().app.message.next(
                get.sources().platform.getString(R.string.data_loading_failed_message)
            )
        }
    }

    this.act(onDone, task)
}

// NOTE: act - действуй!
// (кандидат run() отпал, слишком заезжено и много переопределений что может вызывать путаницу)
inline fun <T> Worker<T>.act(
    crossinline onDone: (T) -> Unit = {},
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
        onDone(data)
    }) {
        return@work task()
    }

}

class ScreensNavigator(val tag: String = "", initialScreen: IScreen? = null) {
    val screens = if (initialScreen != null)
        mutableListOf(initialScreen)
    else
        mutableListOf()
    val screen = UpdatableState(initialScreen)
    var onResume: (() -> Unit)? = null

    fun startScreen(
        screen: IScreen,
        clearAll: Boolean = false,
        clearAfterFirst: Boolean = false,
        fromScreens: Boolean = false,
        onResume: (() -> Unit)? = null
    ) {
        this.onResume = onResume

        if (clearAll) {
            screens.clear()
        }
        if (screens.size > 0 && clearAfterFirst) {
            val first = screens[0]
            screens.clear()
            screens.add(first)
        }

        val nextScreen = if (fromScreens) {
            screens.firstOrNull { it.className() == screen.className() } ?: screen
        } else {
            screen
        }

        log("$tag.startScreen: ${nextScreen.className()}")
        screens.add(nextScreen)

        log("screens: $screens")
        this@ScreensNavigator.screen.next(nextScreen)
    }

    fun backToPrevious() {
        screens.removeAt(screens.lastIndex) // pop

        if (screens.size == 0) {
            screen.next(null)
            log("$tag.backToPrevious: null")

        } else {
            val prev = screens.last()
            screen.next(prev)
            log("$tag.backToPrevious: ${prev.javaClass.simpleName}")
        }

        onResume?.invoke()
    }

    inline fun <reified T : IScreen> getScreenOrNull(klass: KClass<T>): T? {
        return screens.firstOrNull { it.javaClass.simpleName == klass.simpleName } as T?
    }

    fun reset() {
        screens.clear()
        screen.next(null)
    }
}

interface IScreen
