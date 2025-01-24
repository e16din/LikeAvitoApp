package me.likeavitoapp.screens.splash

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.likeavitoapp.DataSources
import me.likeavitoapp.exceptionHandler
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.main.search.SearchScreen


class StartUseCase(
    val scope: CoroutineScope,
    val sources: DataSources<SplashScreen>
) {
    var startMs: Long = System.currentTimeMillis()

    fun run() {
        scope.launch(exceptionHandler) {
            sources.app.currentScreenFlow.emit(SplashScreen())
            sources.app.user.id = sources.platform.userIdStore.load()

            val finishMs = System.currentTimeMillis()
            val delayMs = 1000 - (finishMs - startMs)
            delay(delayMs)

            val nextScreen = if (sources.app.user.id != null)
                SearchScreen()
            else
                AuthScreen()
            sources.app.currentScreenFlow.emit(nextScreen)
        }
    }
}