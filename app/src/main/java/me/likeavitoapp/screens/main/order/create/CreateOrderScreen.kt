package me.likeavitoapp.screens.main.order.create

import me.likeavitoapp.get
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.Order.PickupPoint
import me.likeavitoapp.model.OrderRequest
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.order.create.selectpickup.SelectPickupPointScreen


class CreateOrderScreen(
    val ad: Ad,
    val navigator: ScreensNavigator
) : IScreen {

    inner class State {
        val orderType = UpdatableState(
            if (ad.isDeliveryEnabled)
                Order.Type.Delivery
            else
                Order.Type.Pickup
        )
        var selectedPickupPoint = UpdatableState<PickupPoint?>(null)
    }

    val state = State()

    init {
        if (get.sources().app.activeOrderRequest?.ad?.id != ad.id) {
            get.sources().app.activeOrderRequest = OrderRequest(
                ad = ad,
                type = state.orderType.value
            )
        }
    }

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ClickToOrderTypeUseCase(orderType: Order.Type) {
        recordScenarioStep()

        state.orderType.next(orderType)
    }

    fun ClickToSelectDeliveryAddressUseCase() {
        recordScenarioStep()

        navigator.startScreen(
            SelectDeliveryAddressScreen(
                state.selectedPickupPoint,
                get.sources().app.pickupPointTypes.filter {
                    ad.enabledPickupPointTypes.contains(it.id)
                },
                navigator
            )
        )
    }

    fun ClickToSelectPickupPointUseCase() {
        recordScenarioStep()

        navigator.startScreen(
            SelectPickupPointScreen(
                state.selectedPickupPoint,
                get.sources().app.pickupPointTypes.filter {
                    ad.enabledPickupPointTypes.contains(it.id)
                },
                navigator
            )
        )
    }

}
