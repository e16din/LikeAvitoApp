package me.likeavitoapp.screens.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.likeavitoapp.NavRoutes
import me.likeavitoapp.Route
import me.likeavitoapp.Screen


class MainScreen(
    val input: Input = Input(),
    val state: State = State(),
    override val route: Route = Route(NavRoutes.Main, true),
) : Screen {

    enum class Tabs() {
        Search,
        Favorites,
        Profile
    }

    class Input(
        var onTabSelected: (tab: Tabs) -> Unit = {},
    )

    class State {
        var selectedTab by mutableStateOf(Tabs.Search)
    }
}