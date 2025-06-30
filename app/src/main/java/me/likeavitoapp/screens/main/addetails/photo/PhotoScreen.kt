package me.likeavitoapp.screens.main.addetails.photo

import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.recordScenarioStep


class PhotoScreen(
    photoUrl: String,
    val navigator: ScreensNavigator
) : IScreen {

    class State(
        val url:String
    )

    val state = State(photoUrl)

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }
}
