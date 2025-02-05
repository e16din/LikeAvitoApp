package me.likeavitoapp.screens.main.tabs.profile.edit

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.User
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources

class EditProfileScreen(
    val parentNavigator: ScreensNavigator,
    val scope: CoroutineScope = provideCoroutineScope(),
    val sources: DataSources = provideDataSources(),
    user: User = sources.app.user!!
) : IScreen {

    class State(val user: User)

    val state = State(user)

    fun PressBack() {
        parentNavigator.backToPrevious()
    }
}