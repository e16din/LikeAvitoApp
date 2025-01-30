package me.likeavitoapp.screens.addetails

import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.model.Ad

import me.likeavitoapp.model.IScreen
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.main.MainScreen


class AdDetailsScreen(
    ad: Ad,
    val state: State = State(ad),
    override var prevScreen: IScreen?,
    override var innerScreen: MutableStateFlow<IScreen>? = null
) : IScreen {

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
