package me.likeavitoapp.screens.splash

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.dataSources
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.main.MainScreen


class SplashScreen(
    val sources: DataSources = dataSources(),
) : IScreen {

    val nav: Navigation = Navigation()

    class Navigation(val roots: Roots = Roots()) {
        class Roots {
            fun authScreen() = AuthScreen()
            fun mainScreen() = MainScreen()
        }
        class Pages {}
        class Stack {}
    }

    class State {}

    // UseCases:

    suspend fun StartScreenUseCase(startMs: Long) {
        sources.backend.token = sources.platform.appDataStore.loadToken()
        val userId = sources.platform.appDataStore.loadId()
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

        sources.app.navigator.startScreen(
            if (sources.app.user != null)
                nav.roots.mainScreen()
            else
                nav.roots.authScreen()
        )
    }
}