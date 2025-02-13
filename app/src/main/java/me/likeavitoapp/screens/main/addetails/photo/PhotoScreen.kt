package me.likeavitoapp.screens.main.addetails.photo

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep


class PhotoScreen(
    photoUrl: String,
    val navigator: ScreensNavigator,
    val scope: CoroutineScope = provideCoroutineScope(),
    val sources: DataSources = provideDataSources(),
    val state: State = State(photoUrl)

) : IScreen {

    class State(
        val url:String
    )

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }
}
