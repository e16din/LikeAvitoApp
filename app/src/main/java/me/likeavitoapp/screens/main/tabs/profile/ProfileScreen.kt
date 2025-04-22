package me.likeavitoapp.screens.main.tabs.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.likeavitoapp.developer.primitives.work
import me.likeavitoapp.get
import me.likeavitoapp.model.Chat
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UnauthorizedException
import me.likeavitoapp.model.Worker
import me.likeavitoapp.model.load
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.tabs.chat.ChatScreen
import me.likeavitoapp.screens.main.tabs.profile.edit.EditProfileScreen


class ProfileScreen(
    val navigator: ScreensNavigator
) : IScreen {

    class State(
        val logout: Worker<Unit> = Worker(Unit),
        val chats: Worker<List<Chat>> = Worker(listOf()),
    )

    val state = State()

    fun StartScreenUseCase() {
        recordScenarioStep()

        state.chats.load {
            val result = get.sources().backend.messagesService.getActiveChats()
            return@load Pair(result.getOrNull(), result.isSuccess)
        }
    }

    fun ClickToContactUseCase(label: String, value: String) {
        recordScenarioStep()

        val clipboard = get.appContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, value)
        clipboard.setPrimaryClip(clip)
    }

    fun ClickToEditProfileUseCase() {
        recordScenarioStep()

        navigator.startScreen(
            EditProfileScreen(navigator),
        )
    }

    fun CloseScreenUseCase() {
        recordScenarioStep()

//        state.user.photoUrl.free(ProfileScreen::class)
    }

    fun ClickToLogoutUseCase() {
        recordScenarioStep()

        get.sources().app.loading.next(true)
        work {
            val result = get.sources().backend.userService.logout()

            if (result.isSuccess) {
                withContext(Dispatchers.Main) {
                    throw UnauthorizedException()
                }
            }
        }
    }

    fun ClickToChatUseCase(chat: Chat) {
        recordScenarioStep()

        navigator.startScreen(
            ChatScreen(chat.ad, navigator),
            onResume = {
                val current = state.chats.output.value
                state.chats.output.next(emptyList())
                state.chats.output.next(current)

                get.sources().app.updateNewMessagesIndicator()
                // = update isNew states
            }
        )
    }

}