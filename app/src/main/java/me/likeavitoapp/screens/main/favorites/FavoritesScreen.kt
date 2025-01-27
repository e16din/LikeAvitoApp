package me.likeavitoapp.screens.main.favorites

import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.Screen


class FavoritesScreen(
    val input: Input = Input(),
    val state: State = State(),
    override var prevScreen: Screen? = null,
    override var innerScreen: MutableStateFlow<Screen>? = null,
) : Screen {

    class Input {}
    class State {}
}