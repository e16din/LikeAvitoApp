package me.likeavitoapp.screens.main.order.details

import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.ScreensNavigator

import me.likeavitoapp.recordScenarioStep


class OrderDetailsScreen(
    order: Order,
    val navigator: ScreensNavigator
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
