package me.likeavitoapp.screens

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.develop
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.StateValue
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.splash.SplashScreen

class RootScreen(
    val scope: CoroutineScope,
    val sources: DataSources
) : IScreen {

    class State(
        var isStarted: Boolean = false,
        var demoLabelEnabled: Boolean = develop,
        val scenariosEnabled: StateValue<Boolean> = StateValue(false),
    )

    val state = State()
    val navigator = ScreensNavigator(tag = javaClass.simpleName)

    fun StartScreenUseCase() {
        recordScenarioStep()

        if (state.isStarted) {
            navigator.startScreen(navigator.screens.last())

        } else {
            state.isStarted = true
            navigator.startScreen(
                SplashScreen(parentNavigator = navigator)
            )
        }
    }

    fun LogoutUseCase() {
        scope.launchWithHandler {
            navigator.startScreen(
                AuthScreen(
                    scope = scope,
                    parentNavigator = navigator,
                    sources = sources
                )
            )
            sources.platform.appDataStore.clear()
        }
    }

    fun ClickToDemoDeveloperUseCase() {
        state.scenariosEnabled.value = true
    }
}