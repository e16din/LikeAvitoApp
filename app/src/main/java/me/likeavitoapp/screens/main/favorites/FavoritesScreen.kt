package me.likeavitoapp.screens.main.favorites

import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.model.IScreen


class FavoritesScreen(
    val input: Input = Input(),
    val state: State = State(),
    override var prevScreen: IScreen? = null,
    override var innerScreen: MutableStateFlow<IScreen>? = null,
) : IScreen {

    class Input {}
    class State {}
}