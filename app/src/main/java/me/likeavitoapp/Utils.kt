package me.likeavitoapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.model.Loadable


inline fun <reified T> Loadable<*>.load(
    loading: () -> Result<T>,
    onSuccess: (data: T) -> Unit
) {
    this.loading.value = true

    val result = loading()
    val newData = result.getOrNull()

    this.loading.value = false

    if (newData != null) {
        onSuccess(newData)

    } else {
        this.loadingFailed.value = true
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

fun MutableStateFlow<Boolean>.inverse() {
    this.value = !this.value
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