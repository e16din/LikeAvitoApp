package me.likeavitoapp.screens.splash

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
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
            withContext(Dispatchers.Main) {
                state.contentEnabled.next(true)
            }

            get.sources().backend.token = get.sources().platform.appDataStore.loadToken()
            val userId = get.sources().platform.appDataStore.loadUserId()
            var isAuthorized = false
            val app = get.sources().app
            if (userId != null) {
                val result = get.sources().backend.userService.getUser(userId)
                val user = result.getOrNull()
                if (user != null) {
                    withContext(Dispatchers.Main) {
                        app.user.next(user)
                    }
                    isAuthorized = true
                }
            }

            val categoriesResult = get.sources().backend.adsService.getCategories()
            app.categories = categoriesResult.getOrNull() ?: emptyList()

            val regionsResult = get.sources().backend.adsService.getRegions()
            app.regions = regionsResult.getOrNull() ?: emptyList()

            val typesResult = get.sources().backend.orderService.getPickupPointTypes()
            app.pickupPointTypes = typesResult.getOrNull() ?: emptyList()


            val finishMs = System.currentTimeMillis()
            val delayMs = 1000 - (finishMs - startMs)
            delay(delayMs)

            withContext(Dispatchers.Main) {
                navigator.startScreen(
                    if (isAuthorized)
                        MainScreen().also {
                            app.mainScreen = it
                        }
                    else
                        AuthScreen(navigator = navigator),
                )
            }
        }
    }
}