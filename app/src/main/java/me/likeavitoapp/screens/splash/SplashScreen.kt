package me.likeavitoapp.screens.splash

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.BaseScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.main.MainScreen


class SplashScreen(
    val parentNavigator: ScreensNavigator,
    val scope: CoroutineScope = provideCoroutineScope(),
    val sources: DataSources = provideDataSources()
) : BaseScreen() {

    class State(val contentEnabled: UpdatableState<Boolean> = UpdatableState(false))

    val state = State()
    // UseCases:

    fun StartScreenUseCase(startMs: Long = System.currentTimeMillis()) {
        scope.launchWithHandler {
            delay(200)
            state.contentEnabled.post(true)

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

            parentNavigator.startScreen(
                if (sources.app.user != null)
                    MainScreen()
                else
                    AuthScreen(
                        parentNavigator = parentNavigator
                    )
            )
        }
    }
}