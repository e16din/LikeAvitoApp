package me.likeavitoapp.screens.main.order.create.selectdelivery

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import me.likeavitoapp.R
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
        val query = UpdatableState(
            TextFieldValue(
                get.sources().app.activeOrderRequest!!.deliveryAddress ?: ""
            )
        )

        val addresses = Worker<List<String>>(emptyList())
    }

    val state = State()

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ChangeQueryUseCase(address: TextFieldValue) {
        recordScenarioStep(address)

        state.query.next(address)
        val orderRequest = get.sources().app.activeOrderRequest!!
        orderRequest.deliveryAddress = address.text.ifEmpty { null }

        state.addresses.act {
            val result = get.sources().backend.mapService.getAddressesBy(address.text)
            return@act Pair(result.getOrNull(), result.isSuccess)
        }
    }

    fun ClickToClearAddressUseCase() {
        recordScenarioStep()

        state.query.next(TextFieldValue(""))
        val orderRequest = get.sources().app.activeOrderRequest!!
        orderRequest.deliveryAddress = null
        state.addresses.resetWith(emptyList())
    }

    fun ClickToAddressUseCase(address: String) {
        recordScenarioStep()

        ChangeQueryUseCase(
            TextFieldValue(address, TextRange(address.length))
        )
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
            get.sources().app.message.next(
                get.sources().platform.getString(R.string.select_address_to_next_message)
            )
        }
    }
}
