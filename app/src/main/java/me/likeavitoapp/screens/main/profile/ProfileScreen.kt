package me.likeavitoapp.screens.main.profile

import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.User


class ProfileScreen(
    user: User,
    val input: Input = Input(),
    val state: State = State(user),
    override var prevScreen: IScreen? = null,
    override var innerScreen: MutableStateFlow<IScreen>? = null,
) : IScreen {
    class Input {
        var onEditProfileClick: () -> Unit = {}
        var onBackClick: () -> Unit = {}
    }

    class State(val user: User) {

    }
}