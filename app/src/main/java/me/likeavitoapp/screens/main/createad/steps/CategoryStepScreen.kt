package me.likeavitoapp.screens.main.createad.steps

import me.likeavitoapp.get
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.recordScenarioStep


class CategoryStepScreen(
    val navigator: ScreensNavigator
) : IScreen {

    class State {
        val query = UpdatableState("")

        val selectedCategoryId = UpdatableState(
            get.sources().app.activeCreateAdRequest?.categoryId
        )
    }

    val state = State()

    fun StartScreenUseCase() {
        recordScenarioStep()

    }

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun SelectCategoryIdUseCase(categoryId: Int) {
        recordScenarioStep()

        state.selectedCategoryId.next(categoryId)
    }

    fun ClickToDoneUseCase() {
        recordScenarioStep()

        get.sources().app.activeCreateAdRequest?.categoryId =
            state.selectedCategoryId.value

        navigator.startScreen(
            DeliveryStepScreen(navigator)
        )
    }

    fun ChangeQueryUseCase(query: String) {
        recordScenarioStep(query)

        state.query.next(query)
    }

    fun ClickToClearQueryUseCase() {
        recordScenarioStep()

        state.query.next("")
    }
}