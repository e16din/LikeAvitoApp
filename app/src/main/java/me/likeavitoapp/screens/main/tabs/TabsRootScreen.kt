package me.likeavitoapp.screens.main.tabs

import me.likeavitoapp.className
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.screens.main.tabs.search.SearchScreen

class TabsRootScreen(initialScreen: SearchScreen) : IScreen {

    class State()

    val state = State()
    val navigator = ScreensNavigator(
        tag = className(),
        initialScreen = initialScreen
    )
}