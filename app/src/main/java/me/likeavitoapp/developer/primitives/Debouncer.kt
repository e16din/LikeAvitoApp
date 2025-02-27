package me.likeavitoapp.developer.primitives

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class Debouncer<T>(var value: T, val timeoutMs: Long = 390L, val onTimeout: (value: T) -> Unit) {
    var job: Job? = null

    fun set(newValue: T) {
        value = newValue

        job?.cancel()
        job = work(onDone = {
            onTimeout(value)
        }) {
            delay(timeoutMs)
        }
    }
}