package me.likeavitoapp.screens.main.tabs.favorites

import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.model.IScreen


class FavoritesScreen(
    val input: Input = Input(),
    val state: State = State(),
) : IScreen {

    class Input {}
    class State {}
}