package me.likeavitoapp.screens.main.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import me.likeavitoapp.Route
import me.likeavitoapp.RouteTabStub
import me.likeavitoapp.Screen
import me.likeavitoapp.User


class ProfileScreen(
    user: User,
    val input: Input = Input(),
    val state: State = State(user),
    override val route: Route = RouteTabStub
) : Screen {
    class Input {
        var onEditProfileClick: () -> Unit = {}
        var onBackClick: () -> Unit = {}
    }

    class State(user: User) {
        val user by mutableStateOf(user)
    }
}