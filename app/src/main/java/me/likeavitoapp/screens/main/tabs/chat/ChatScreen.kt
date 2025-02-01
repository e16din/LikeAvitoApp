package me.likeavitoapp.screens.main.tabs.chat

import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.dataSources


class ChatScreen(
    ad: Ad,
    val sources: DataSources = dataSources(),
    ) : IScreen {

    val state = State(ad)

    class State(val ad: Ad)

}
