package me.likeavitoapp

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.likeavitoapp.model.Worker
import me.likeavitoapp.model.UpdatableState

fun Any.className(): String {
    return javaClass.simpleName
}

suspend inline fun <T> MutableState<T>.setUi(value: T) {
    withContext(Dispatchers.Main) {
        this@setUi.value = value
    }
}

suspend inline fun <reified T> Worker<*>.load(
    loading: () -> Result<T>,
    crossinline onSuccess: (data: T) -> Unit
) {
    this.working.post(true)

    val result = loading()
    val newData = result.getOrNull()

    this.working.post(false)

    if (newData != null) {
        withContext(defaultContext + Dispatchers.Main) {
            onSuccess(newData)
        }

    } else {
        log("!!!loadingFailed!!!")
        this@load.fail.post(true)
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