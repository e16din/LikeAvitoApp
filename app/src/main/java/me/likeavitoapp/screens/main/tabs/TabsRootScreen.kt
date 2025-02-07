package me.likeavitoapp.screens.main.tabs

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.className
import me.likeavitoapp.develop
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.splash.SplashScreen

class TabsRootScreen(
    val scope: CoroutineScope,
    val sources: DataSources
) : IScreen {

    class State()

    val state = State()
    val navigator = ScreensNavigator(tag = className())

}