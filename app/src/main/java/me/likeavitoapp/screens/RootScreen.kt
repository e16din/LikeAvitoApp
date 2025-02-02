package me.likeavitoapp.screens

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.develop
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.splash.SplashScreen

class RootScreen(
    val scope: CoroutineScope,
    val sources: DataSources
) : IScreen {

    class State(
        val demoLabelEnabled: Boolean = develop,
        val scenariosEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    )

    val state = State()
    val navigator = ScreensNavigator(tag = javaClass.simpleName)

    fun StartScreenUseCase() {
        recordScenarioStep()

        navigator.screen.value = SplashScreen(parentNavigator = navigator)
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