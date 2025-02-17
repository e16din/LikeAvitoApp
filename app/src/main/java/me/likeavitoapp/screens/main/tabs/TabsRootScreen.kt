package me.likeavitoapp.screens.main.tabs

import me.likeavitoapp.className
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator

class TabsRootScreen() : IScreen {

    class State()

    val state = State()
    val navigator = ScreensNavigator(tag = className())

}