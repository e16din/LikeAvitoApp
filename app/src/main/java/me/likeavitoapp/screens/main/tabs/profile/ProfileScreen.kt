package me.likeavitoapp.screens.main.tabs.profile

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.User
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources


class ProfileScreen(
    user: User,
    val parentNavigator: ScreensNavigator,
    val scope: CoroutineScope = provideCoroutineScope(),
    val sources: DataSources = provideDataSources()
) : IScreen {

    class State(val user: User)

    val state = State(user)

    lateinit var navigator: ScreensNavigator

//    var onEditProfileClick: () -> Unit = {}
//    var onBackClick: () -> Unit = {}
}