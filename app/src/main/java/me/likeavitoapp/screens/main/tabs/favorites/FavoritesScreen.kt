package me.likeavitoapp.screens.main.tabs.favorites

import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.dataSources


class FavoritesScreen(
    val parentNavigator: ScreensNavigator,
    val sources: DataSources = dataSources()
) : IScreen {

    class State {}

    val state: State = State()
    lateinit var navigator : ScreensNavigator
}