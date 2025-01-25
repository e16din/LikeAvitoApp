package me.likeavitoapp.screens.main.favorites

import me.likeavitoapp.Route
import me.likeavitoapp.RouteTabStub
import me.likeavitoapp.Screen


class FavoritesScreen(
    val input: Input = Input(),
    val state: State = State(),
    override val route: Route = RouteTabStub
) : Screen {
    class Input {}
    class State {}
}