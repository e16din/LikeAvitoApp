package me.likeavitoapp.screens.main.tabs.chat

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources


class ChatScreen(
    ad: Ad,
    val parentNavigator: ScreensNavigator,
    val scope: CoroutineScope = provideCoroutineScope(),
    val sources: DataSources = provideDataSources()
) : IScreen {

    val state = State(ad)

    class State(val ad: Ad)

}
