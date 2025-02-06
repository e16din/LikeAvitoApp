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
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.LoadableState
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass


suspend inline fun <T> MutableState<T>.setUi(value: T) {
    withContext(Dispatchers.Main) {
        this@setUi.value = value
    }
}

@Composable
fun <T : R, R> UpdatableState<T>.collectAsState(
    key: KClass<*> = Unit::class,
    initial: R = this.value,
    context: CoroutineContext = EmptyCoroutineContext
): State<R> = produceState(initial, this.value) {
    if (context == EmptyCoroutineContext) {
        withContext(Dispatchers.Main) {
            listen(key) { value = it }
        }
    } else withContext(context) {
        listen(key) { value = it }
    }
}

@Composable
fun <V, T : List<V>> UpdatableState<T>.collectAsMutableStateList(
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
    this.loading.post(true)

    val result = loading()
    val newData = result.getOrNull()

    this.loading.post(false)

    if (newData != null) {
        withContext(defaultContext + Dispatchers.Main) {
            onSuccess(newData)
        }

    } else {
        log("!!!loadingFailed!!!")
        this@load.loadingFailed.post(true)
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


fun UpdatableState<Boolean>.inverse() {
    this.post(!this.value)
}

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