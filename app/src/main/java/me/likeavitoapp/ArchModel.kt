package me.likeavitoapp

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

@Suppress("UNCHECKED_CAST")
class DataSources<T : Screen>(
    val app: AppModel = AppPlatform.get.app,
    val screen: T = app.screens.last() as T,
    val platform: AppPlatform = AppPlatform.get,
    val backend: Backend = AppPlatform.get.backend
)

class Loadable<T>(initial: T) {
    var data by mutableStateOf(initial)
    var loading by mutableStateOf(false)
    var loadingFailed by mutableStateOf(false)
}

class UseCaseResult<T : Screen>(
    val sources: DataSources<T>,
    val scope: CoroutineScope,
    val job: Job? = null
)