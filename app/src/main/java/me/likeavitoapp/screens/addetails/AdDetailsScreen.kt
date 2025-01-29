package me.likeavitoapp.screens.addetails

import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.Ad

import me.likeavitoapp.Screen
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.main.MainScreen


class AdDetailsScreen(
    ad: Ad,
    val state: State = State(ad),
    override var prevScreen: Screen?,
    override var innerScreen: MutableStateFlow<Screen>? = null
) : Screen {

    class Input {}
    class State(val ad: Ad) {
    }
    class Navigation(val roots: Roots = Roots()) {
        class Roots {
            fun authScreen() = AuthScreen()
            fun mainScreen() = MainScreen()
        }
    }
}
