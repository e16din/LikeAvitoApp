package me.likeavitoapp.screens.main.order.details

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.MainSet
import me.likeavitoapp.mainSet
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.mainSet

import me.likeavitoapp.recordScenarioStep


class OrderDetailsScreen(
    order: Order,
    val navigator: ScreensNavigator,
    val scope: CoroutineScope = mainSet.provideCoroutineScope(),
    val sources: DataSources = mainSet.provideDataSources()
) : IScreen {

    class State(val order: Order)

    val state = State(order)

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun CloseScreenUseCase() {
        recordScenarioStep()
    }
}
