package me.likeavitoapp.screens.main.tabs.chat

import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.MainSet
import me.likeavitoapp.mainSet
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IMessage
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.mainSet

import me.likeavitoapp.recordScenarioStep


class ChatScreen(
    ad: Ad,
    val navigator: ScreensNavigator,

    val scope: CoroutineScope = mainSet.provideCoroutineScope(),
    val sources: DataSources = mainSet.provideDataSources()
) : IScreen {

    class State(
        val ad: Ad,
        val messages: SnapshotStateList<IMessage> = SnapshotStateList(),
        val message: UpdatableState<String> = UpdatableState("")
    )

    val state = State(ad)

    fun ChangeMessageUseCase(newMessage: String) {
        recordScenarioStep(newMessage)

        state.message.post(newMessage)
    }

    fun ClickToSendUseCase() {
        recordScenarioStep()

        state.message.post("")
    }

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }
}
