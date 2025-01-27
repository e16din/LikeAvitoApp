package me.likeavitoapp.screens.splash

import kotlinx.coroutines.delay
import me.likeavitoapp.DataSources
import me.likeavitoapp.Screen
import me.likeavitoapp.dataSources
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.main.MainScreen


class SplashScreen(
    val sources: DataSources = dataSources(),
    override var prevScreen: Screen? = null,
    override var innerScreen: Screen? = null
) : Screen {

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

    suspend fun StartApp(startMs: Long) {
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

        sources.app.currentScreen.value =
            if (sources.app.user != null)
                nav.roots.mainScreen()
            else
                nav.roots.authScreen()
    }
}