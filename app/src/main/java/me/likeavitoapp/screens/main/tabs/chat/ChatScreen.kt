package me.likeavitoapp.screens.main.tabs.chat

import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.likeavitoapp.developer.primitives.work
import me.likeavitoapp.get
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.IMessage
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.PreviewTextMessage
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.recordScenarioStep


class ChatScreen(
    val ad: Ad,
    val navigator: ScreensNavigator
) : IScreen {

    class State(
        val messages: SnapshotStateList<IMessage> = SnapshotStateList(),
        val message: UpdatableState<String> = UpdatableState("")
    )

    val state = State()

    fun StartScreenUseCase() {
        recordScenarioStep()
        work {
            val user = get.sources().app.user.value!!
            get.sources().backend.messagesService
                .listenChatUpdates(user.id, ad.owner.id) { newMessages ->
                    withContext(Dispatchers.Main) {
                        state.messages.addAll(newMessages)
                    }
                }
        }
    }

    fun ChangeMessageUseCase(newMessage: String) {
        recordScenarioStep(newMessage)

        state.message.next(newMessage)
    }

    fun ClickToSendUseCase() {
        recordScenarioStep()

        val text = state.message.value
        val preview = PreviewTextMessage(text, true)
        state.messages.add(preview)
        state.message.next("")

        work<Unit> {
            val result = get.sources().backend.messagesService.sendMessage(
                get.sources().app.user.value!!.id,
                ad.owner.id,
                text
            )
            result.getOrNull()?.let {
                withContext(Dispatchers.Main) {
                    state.messages.remove(preview)
                    state.messages.add(it)
                }
            }
        }
    }

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }
}
