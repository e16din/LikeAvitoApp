package me.likeavitoapp.screens.main.order.create

import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.Order.PickupPoint
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.order.create.payment.PaymentScreen
import me.likeavitoapp.screens.main.order.create.selectpickup.SelectPickupScreen


class CreateOrderScreen(
    val ad: Ad,
    val navigator: ScreensNavigator
) : IScreen {

    class State {
        val orderType = UpdatableState(Order.Type.Delivery)
        var selectedPickupPoint = UpdatableState<PickupPoint?>(null)
    }

    val state = State()

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ClickToOrderTypeUseCase(orderType: Order.Type) {
        recordScenarioStep()

        state.orderType.post(orderType)
    }

    fun ClickToPickupUseCase() {
        recordScenarioStep()

        navigator.startScreen(
            SelectPickupScreen(state.selectedPickupPoint, navigator)
        )
    }

    fun ClickToOrderUseCase() {
        recordScenarioStep()

        navigator.startScreen(
            PaymentScreen(ad, state.orderType.value,  navigator)
        )
    }

}
