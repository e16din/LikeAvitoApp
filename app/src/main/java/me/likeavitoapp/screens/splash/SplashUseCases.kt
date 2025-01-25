package me.likeavitoapp.screens.splash

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.likeavitoapp.DataSources
import me.likeavitoapp.UseCaseResult
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.main.MainScreen


fun StartAppUseCase(
    scope: CoroutineScope,
    sources: DataSources<SplashScreen>,
    startMs: Long
): UseCaseResult<SplashScreen> {
    val job = scope.launch {
        sources.app.currentScreenFlow.emit(SplashScreen())
        sources.backend.token = sources.platform.authDataStore.loadToken()
        val userId = sources.platform.authDataStore.loadId()
        if (userId != null) {
            val result = sources.backend.userService.getUser(userId)
            val user = result.getOrNull()
            if (user != null) {
                sources.app.user = user
            }
        }

        val finishMs = System.currentTimeMillis()
        val delayMs = 1000 - (finishMs - startMs)
        delay(delayMs)

        val nextScreen = if (sources.app.user != null)
            MainScreen()
        else
            AuthScreen()
        sources.app.currentScreenFlow.emit(nextScreen)
    }

    return UseCaseResult(sources, scope, job)
}


