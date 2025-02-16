package me.likeavitoapp.screens.splash

import kotlinx.coroutines.delay
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.get
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState

import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.main.MainScreen


class SplashScreen(val navigator: ScreensNavigator) : IScreen {

    class State(val contentEnabled: UpdatableState<Boolean> = UpdatableState(false))

    val state = State()
    // UseCases:

    fun StartScreenUseCase(startMs: Long = System.currentTimeMillis()) {
        get.scope().launchWithHandler {
            delay(200)
            state.contentEnabled.post(true)

            get.sources().backend.token = get.sources().platform.appDataStore.loadToken()
            val userId = get.sources().platform.appDataStore.loadId()
            var isAuthorized = false
            if (userId != null) {
                val result = get.sources().backend.userService.getUser(userId)
                val user = result.getOrNull()
                if (user != null) {
                    get.sources().app.user.post(user)
                    isAuthorized = true
                }
            }

            val finishMs = System.currentTimeMillis()
            val delayMs = 1000 - (finishMs - startMs)
            delay(delayMs)

            navigator.startScreen(
                if (isAuthorized)
                    MainScreen()
                else
                    AuthScreen(navigator = navigator)
            )
        }
    }
}