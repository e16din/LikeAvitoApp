package me.likeavitoapp.screens.main.tabs.chat

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.likeavitoapp.developer.primitives.work
import me.likeavitoapp.get
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.IMessage
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.PreviewTextMessage
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.TextMessage
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.recordScenarioStep


class ChatScreen(
    val ad: Ad,
    val navigator: ScreensNavigator,
    val initialMessages: List<TextMessage>? = null
) : IScreen {

    inner class State(
        val userId: Long = get.sources().app.user.value!!.id,
        val messages: SnapshotStateList<IMessage> = initialMessages?.toMutableStateList()
            ?: SnapshotStateList(),
        val message: UpdatableState<String> = UpdatableState(""),
        val scrollToEnd: UpdatableState<Boolean> = UpdatableState(false)
    )

    val state = State()

    fun StartScreenUseCase() {
        recordScenarioStep()

        get.sources().app.loading.next(true)
        work {
            if (initialMessages == null) {
                val result = get.sources().backend.messagesService.loadChat(ad.id)
                withContext(Dispatchers.Main) {
                    get.sources().app.loading.next(false)

                    result.getOrNull()?.let {
                        state.messages.addAll(it.messages)
                        state.scrollToEnd.next(true)
                    }
                }
            }

            get.sources().backend.messagesService
                .listenChatUpdates(ad.id) { newMessages ->
                    withContext(Dispatchers.Main) {
                        state.messages.addAll(newMessages)
                        state.scrollToEnd.next(true)
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

        val userId = get.sources().app.user.value!!.id

        val text = state.message.value
        val preview = PreviewTextMessage(text, userId, true)
        state.messages.add(preview)
        state.message.next("")


        work<Unit> {
            val result = get.sources().backend.messagesService.sendMessage(
                ad.id,
                text
            )
            result.getOrNull()?.let {
                withContext(Dispatchers.Main) {
                    state.messages.remove(preview)
                    state.messages.add(it)
                    state.scrollToEnd.next(true)
                }
            }
        }
    }

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }
}
