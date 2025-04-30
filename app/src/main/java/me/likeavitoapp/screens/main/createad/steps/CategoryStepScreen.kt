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
        val categories = UpdatableState(get.sources().app.categories)

        val selectedCategoryId = UpdatableState(
            get.sources().app.activeCreateAdRequest?.categoryId
        )
    }

    val state = State()

    fun SelectCategoryIdUseCase(categoryId: Int) {
        recordScenarioStep()

        state.selectedCategoryId.next(categoryId)
        get.sources().app.activeCreateAdRequest!!.categoryId = categoryId
    }



    fun ChangeQueryUseCase(query: String) {
        recordScenarioStep(query)

        state.query.next(query)

        state.categories.next(
            get.sources().app.categories.filter {
                it.name.lowercase().contains(query.lowercase())
            }
        )
    }

    fun ClickToClearQueryUseCase() {
        recordScenarioStep()

        ChangeQueryUseCase("")
    }
}