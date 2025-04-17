package me.likeavitoapp.screens.main.order.create.selectdelivery

import me.likeavitoapp.get
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.Worker
import me.likeavitoapp.model.act
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.order.create.payment.PaymentScreen


class SelectDeliveryAddressScreen(
    val navigator: ScreensNavigator
) : IScreen {

    inner class State {
        val query = UpdatableState("")
        val addresses = Worker<List<String>>(emptyList())
    }

    val state = State()

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ChangeQueryUseCase(query: String) {
        recordScenarioStep(query)

        state.query.next(query)

        state.addresses.act {
            val result = get.sources().backend.mapService.getAddressesBy(query)
            return@act Pair(result.getOrNull(), result.isSuccess)
        }
    }

    fun ClickToClearAddress() {
        state.query.next("")
        state.addresses.resetWith(emptyList())
    }

    fun ClickToAddressUseCase(address: String) {
        state.query.next(address)
        state.addresses.resetWith(emptyList())
    }

    fun ClickToCloseUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ClickToDoneUseCase() {
        recordScenarioStep()

        val orderRequest = get.sources().app.activeOrderRequest!!
        if (orderRequest.deliveryAddress != null) {
            navigator.startScreen(
                PaymentScreen(navigator)
            )

        } else {
            // please select address to continue
        }
    }
}
