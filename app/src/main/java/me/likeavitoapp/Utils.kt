package me.likeavitoapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.screens.main.tabs.favorites.Listener


suspend inline fun <T> MutableState<T>.setUi(value:T){
    withContext(Dispatchers.Main) {
        this@setUi.value = value
    }
}

suspend inline fun <T> Listener<T>.setUi(value:T){
    withContext(Dispatchers.Main) {
        this@setUi.value = value
    }
}

suspend inline fun <reified T> Loadable<*>.load(
    loading: () -> Result<T>,
    onSuccess: (data: T) -> Unit
) {
    this.loading.setUi(true)

    val result = loading()
    val newData = result.getOrNull()

    this.loading.setUi(false)

    if (newData != null) {
        onSuccess(newData)

    } else {
        this.loadingFailed.setUi(true)
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

fun MutableState<Boolean>.inverse() {
    this.value = !this.value
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