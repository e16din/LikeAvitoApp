package me.likeavitoapp.screens.main.addetails.photo

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.MainSet
import me.likeavitoapp.mainSet
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.mainSet

import me.likeavitoapp.recordScenarioStep


class PhotoScreen(
    photoUrl: String,
    val navigator: ScreensNavigator,

    val scope: CoroutineScope = mainSet.provideCoroutineScope(),
    val sources: DataSources = mainSet.provideDataSources(),
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
