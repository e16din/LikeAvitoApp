package me.likeavitoapp.screens.main.addetails

import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.IMessage
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState

import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.addetails.photo.PhotoScreen
import me.likeavitoapp.screens.main.tabs.BaseAdContainerScreen
import me.likeavitoapp.screens.main.tabs.chat.ChatScreen


class AdDetailsScreen(
    ad: Ad,
    override val navigator: ScreensNavigator = ScreensNavigator(),
    override val state: State = State(ad)
) : BaseAdContainerScreen(navigator, state) {

    class State(
        val ad: Ad,
        val messages: UpdatableState<List<IMessage>> = UpdatableState(emptyList()),
    ) : BaseAdContainerState()


    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    override fun CloseScreenUseCase() {
        super.CloseScreenUseCase()

        state.ad.timerLabel.free(AdDetailsScreen::class)
    }

    fun ClickToPhotoUseCase(url: String) {
        recordScenarioStep()

        navigator.startScreen(
            PhotoScreen(url, navigator)
        )
    }

    fun ClickToOpenChatUseCase() {
        recordScenarioStep()
        println("!!! ClickToOpenChatUseCase")
        navigator.startScreen(
            ChatScreen(state.ad, navigator)
        )
    }
}
