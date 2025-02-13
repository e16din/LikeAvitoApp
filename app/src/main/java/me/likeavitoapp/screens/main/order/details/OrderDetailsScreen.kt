package me.likeavitoapp.screens.main.order.details

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep


class OrderDetailsScreen(
    order: Order,
    val navigatorPrev: ScreensNavigator,
    val scope: CoroutineScope = provideCoroutineScope(),
    val sources: DataSources = provideDataSources()
) : IScreen {

    class State(val order: Order)

    val state = State(order)
    val navigatorNext = ScreensNavigator()

    fun PressBackUseCase() {
        recordScenarioStep()

        navigatorPrev.backToPrevious()
    }

    fun CloseScreenUseCase() {
        recordScenarioStep()
    }
}
