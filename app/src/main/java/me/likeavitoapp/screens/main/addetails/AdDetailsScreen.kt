package me.likeavitoapp.screens.main.addetails

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IMessage
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.addetails.photo.PhotoScreen
import me.likeavitoapp.screens.main.tabs.BaseAdContainerScreen
import me.likeavitoapp.screens.main.tabs.chat.ChatScreen


class AdDetailsScreen(
    ad: Ad,
    val navigatorPrev: ScreensNavigator = ScreensNavigator(),
    override val navigatorNext: ScreensNavigator,
    override val scope: CoroutineScope = provideCoroutineScope(),
    override val sources: DataSources = provideDataSources(),
    override val state: State = State(ad)
) : BaseAdContainerScreen(navigatorNext, scope, sources, state) {

    class State(
        val ad: Ad,
        val messages: UpdatableState<List<IMessage>> = UpdatableState(emptyList()),
    ) : BaseAdContainerState()


    fun PressBackUseCase() {
        recordScenarioStep()

        navigatorPrev.backToPrevious()
    }

    override fun CloseScreenUseCase() {
        super.CloseScreenUseCase()

        state.ad.timerLabel.free(AdDetailsScreen::class)
    }

    fun ClickToPhotoUseCase(url: String) {
        recordScenarioStep()

        navigatorNext.startScreen(
            PhotoScreen(url, navigatorNext)
        )
    }

    fun ClickToOpenChatUseCase() {
        recordScenarioStep()

        navigatorNext.startScreen(
            ChatScreen(state.ad, navigatorNext)
        )
    }
}
