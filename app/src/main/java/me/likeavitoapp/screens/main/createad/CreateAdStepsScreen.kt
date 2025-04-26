package me.likeavitoapp.screens.main.createad

import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.createad.steps.DescriptionStepScreen


class CreateAdStepsScreen(
    val navigator: ScreensNavigator
) : IScreen {

    class State

    val state = State()

    fun StartScreenUseCase() {
        recordScenarioStep()

        navigator.startScreen(
            DescriptionStepScreen(navigator)
        )
    }

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }
}