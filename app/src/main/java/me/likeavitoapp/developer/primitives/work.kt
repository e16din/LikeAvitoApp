package me.likeavitoapp.developer.primitives

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import me.likeavitoapp.get
import me.likeavitoapp.launchWithHandler

inline fun <T : Any> work(
    crossinline onDone: (T) -> Unit = {},
    crossinline task: suspend () -> T
): Job {
    return get.scope().launchWithHandler {
        val result = task()

        debug {
            println("work result: $result")
        }

        withContext(Dispatchers.Main) {
            onDone(result)
        }
    }
}