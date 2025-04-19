package me.likeavitoapp.screens.main.addetails

import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.addetails.photo.PhotoScreen
import me.likeavitoapp.screens.main.tabs.BaseAdContainerScreen


class AdDetailsScreen(
    val ad: Ad,
    override val navigator: ScreensNavigator,
    override val state: State = State()
) : BaseAdContainerScreen(navigator, state) {

    class State(
    ) : BaseAdContainerState()

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    override fun CloseScreenUseCase() {
        super.CloseScreenUseCase()

        ad.timerLabel.free(AdDetailsScreen::class)
    }

    fun ClickToPhotoUseCase(url: String) {
        recordScenarioStep()

        navigator.startScreen(
            PhotoScreen(url, navigator)
        )
    }
}
