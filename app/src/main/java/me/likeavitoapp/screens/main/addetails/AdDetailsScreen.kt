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
import me.likeavitoapp.screens.main.tabs.BaseAdScreen
import me.likeavitoapp.screens.main.tabs.chat.ChatScreen


class AdDetailsScreen(
    ad: Ad,
    override val parentNavigator: ScreensNavigator,
    override val scope: CoroutineScope = provideCoroutineScope(),
    override val sources: DataSources = provideDataSources(),
    val navigator: ScreensNavigator = ScreensNavigator()
) : BaseAdScreen(navigator, scope, sources) {

    class State(
        val ad: Ad,
        val messages: UpdatableState<List<IMessage>> = UpdatableState(emptyList()),
    ) : BaseAdState()

    override val state: State = State(ad)

    fun PressBackUseCase() {
        recordScenarioStep()

        parentNavigator.backToPrevious()
    }

    override fun CloseScreenUseCase() {
        super.CloseScreenUseCase()

        with(AdDetailsScreen::class) {
            state.ad.isFavorite.free(this)
            state.ad.timerLabel.free(this)
        }
    }

    fun ClickToPhotoUseCase(url: String) {
        recordScenarioStep()

        navigator.startScreen(
            PhotoScreen(url, navigator)
        )
    }
}
