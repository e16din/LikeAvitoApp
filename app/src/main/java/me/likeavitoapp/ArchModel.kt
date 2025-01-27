package me.likeavitoapp

import kotlinx.coroutines.flow.MutableStateFlow


class DataSources(
    val app: AppModel,
    val platform: AppPlatform,
    val backend: Backend
)

class Loadable<T>(initial: T) {
    var data = MutableStateFlow(initial)
    var loading = MutableStateFlow(false)
    var loadingFailed = MutableStateFlow(false)
}