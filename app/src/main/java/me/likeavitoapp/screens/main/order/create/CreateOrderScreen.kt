package me.likeavitoapp.screens.main.order.create

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Order.PickupPoint
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.order.create.payment.PaymentScreen
import me.likeavitoapp.screens.main.order.create.selectpickup.SelectPickupScreen


class CreateOrderScreen(
    ad: Ad,
    val navigatorPrev: ScreensNavigator,
    val scope: CoroutineScope = provideCoroutineScope(),
    val sources: DataSources = provideDataSources()
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
    val navigatorNext = ScreensNavigator()

    fun PressBackUseCase() {
        recordScenarioStep()

        navigatorPrev.backToPrevious()
    }

    fun ClickToOrderTypeUseCase(orderType: OrderType) {
        recordScenarioStep()

        state.orderType.post(orderType)
    }

    fun ClickToPickupUseCase() {
        recordScenarioStep()

        navigatorNext.startScreen(
            SelectPickupScreen(state.selectedPickupPoint, navigatorNext))
    }

    fun ClickToOrderUseCase() {
        recordScenarioStep()

        navigatorNext.startScreen(
            PaymentScreen(state.ad, navigatorNext, navigatorPrev)
        )
    }

}
