package me.likeavitoapp.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.likeavitoapp.Loadable
import me.likeavitoapp.NavRoutes
import me.likeavitoapp.Route
import me.likeavitoapp.Screen


class AuthScreen(
    val input: Input = Input(),
    val state: State = State(),
    override val route: Route = Route(NavRoutes.Auth, true)
) : Screen {
    class Input(
        var onEmailChanged: (email: String) -> Unit = {},
        var onPassword: (password: String) -> Unit = {},
        var onLogin: () -> Unit = {},
        var onErrorToastClick: () -> Unit = {}
    )

    class State {
        var email by mutableStateOf("")
        var password by mutableStateOf("")

        var emailErrorEnabled by mutableStateOf(false)
        var loginButtonEnabled by mutableStateOf(false)

        var login = Loadable(emptyList<Unit>())
    }
}