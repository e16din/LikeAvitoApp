package me.likeavitoapp.screens.main.profile

import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.Screen
import me.likeavitoapp.User


class ProfileScreen(
    user: User,
    val input: Input = Input(),
    val state: State = State(user),
    override var prevScreen: Screen? = null,
    override var innerScreen: MutableStateFlow<Screen>? = null,
) : Screen {
    class Input {
        var onEditProfileClick: () -> Unit = {}
        var onBackClick: () -> Unit = {}
    }

    class State(val user: User) {

    }
}