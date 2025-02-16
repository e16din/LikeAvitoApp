package me.likeavitoapp.screens.main.order.create

import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Order.PickupPoint
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState

import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.order.create.payment.PaymentScreen
import me.likeavitoapp.screens.main.order.create.selectpickup.SelectPickupScreen


class CreateOrderScreen(
    ad: Ad,
    val navigator: ScreensNavigator
) : IScreen {

    enum class OrderType(val text:String) {
        Delivery("Доставка"),
        Pickup("Самовывоз")
    }

    class State(val ad: Ad) {
        val orderType = UpdatableState(OrderType.Delivery)
        var selectedPickupPoint = UpdatableState<PickupPoint?>(null)
        var hasPayment = UpdatableState(false)
    }

    val state = State(ad)

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ClickToOrderTypeUseCase(orderType: OrderType) {
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
            PaymentScreen(state.ad, navigator)
        )
    }

}
