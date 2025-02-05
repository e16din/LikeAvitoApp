package me.likeavitoapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.likeavitoapp.model.StateValue
import me.likeavitoapp.model.Loadable
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass


suspend inline fun <T> MutableState<T>.setUi(value: T) {
    withContext(Dispatchers.Main) {
        this@setUi.value = value
    }
}

@Composable
fun <T : R, R> StateValue<T>.collectAsState(
    key: KClass<*>,
    initial: R = this.value,
    context: CoroutineContext = EmptyCoroutineContext
): State<R> = produceState(initial, this.value) {
    if (context == EmptyCoroutineContext) {
        listen(key) { value = it }
    } else withContext(context) {
        listen(key) { value = it }
    }
}

@Composable
fun <V, T : List<V>> StateValue<T>.collectAsMutableStateList(
    key: Any,
    context: CoroutineContext = EmptyCoroutineContext
): State<SnapshotStateList<V>> {

    return produceState(
        (this.value as List<V>).toMutableStateList(),
        (this.value as List<V>).toMutableStateList(),
        context
    ) {
        if (context == EmptyCoroutineContext) {
            listen(key) { value = (it as List<V>).toMutableStateList() }
        } else withContext(context) {
            listen(key) { value = (it as List<V>).toMutableStateList() }
        }
    }
}

suspend inline fun <reified T> Loadable<*>.load(
    loading: () -> Result<T>,
    crossinline onSuccess: (data: T) -> Unit
) {
    this.loading.set(true)

    val result = loading()
    val newData = result.getOrNull()


    this.loading.set(false)

    withContext(defaultContext + Dispatchers.Main) {
        if (newData != null) {
            onSuccess(newData)

        } else {
            this@load.loadingFailed.set(true)
        }
    }
}


class Debouncer<T>(var value: T, timeoutMs: Long = 390L, onTimeout: (value: T) -> Unit) {
    var lastSetTimeMs = System.currentTimeMillis()

    init {
        while (true) {
            if (System.currentTimeMillis() - lastSetTimeMs >= timeoutMs) {
                onTimeout(value)
                break
            }
        }
    }

    fun set(newValue: T) {
        value = newValue
        lastSetTimeMs = System.currentTimeMillis()
    }
}


fun StateValue<Boolean>.inverse() {
    this.set(!this.value)
}

//fun MutableState<Boolean>.inverse() {
//    this.value = !this.value
//}
//
//fun MutableStateFlow<Boolean>.inverse() {
//    this.value = !this.value
//}

@Composable
fun Launcher(
    launchOnce: suspend CoroutineScope.(scope: CoroutineScope) -> Unit,
    content: @Composable () -> Unit
) {
    var launched by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        launchOnce.invoke(this, this)
        launched = true
    }

    if (launched) {
        content.invoke()
    }
}