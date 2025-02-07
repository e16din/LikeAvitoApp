package me.likeavitoapp.screens.main.order.create

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep


class CreateOrderScreen(
    ad: Ad,
    val parentNavigator: ScreensNavigator,
    val scope: CoroutineScope = provideCoroutineScope(),
    val sources: DataSources = provideDataSources()
) : IScreen {

    enum class OrderType(val text:String) {
        Delivery("Доставка"),
        Pickup("Самовывоз")
    }

    class State(
        val ad: Ad,
        var orderType: UpdatableState<OrderType> = UpdatableState(OrderType.Delivery)
    )

    fun PressBack() {
        recordScenarioStep()

        parentNavigator.backToPrevious()
    }

    fun ClickToOrderTypeUseCase(orderType: OrderType) {
        recordScenarioStep()

        state.orderType.post(orderType)
    }

    val state = State(ad)

}
