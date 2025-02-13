package me.likeavitoapp.screens.main.order.create.payment

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.PaymentData
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.order.details.OrderDetailsScreen


class PaymentScreen(
    val ad: Ad,
    val navigatorPrev: ScreensNavigator,
    val navigatorNext: ScreensNavigator,
    val scope: CoroutineScope = provideCoroutineScope(),
    val sources: DataSources = provideDataSources()
) : IScreen {

    class State() {
        val payment = Loadable<Order?>(null)
        val paymentData = PaymentData()
    }

    val state = State()

    fun PressBackUseCase() {
        recordScenarioStep()

        navigatorPrev.backToPrevious()
    }

    fun ClickToCloseUseCase() {
        recordScenarioStep()

        navigatorPrev.backToPrevious()
    }

    fun ClickToDoneUseCase() {
        recordScenarioStep()

        navigatorPrev.backToPrevious()
    }

    fun ClickToPayUseCase() {
        recordScenarioStep()

        scope.launchWithHandler {
            state.payment.load(loading = {
                return@load sources.backend.orderService.pay(ad, state.paymentData)
            }, onSuccess = { order ->
                state.payment.data.post(order)
                navigatorNext.startScreen(
                    screen = OrderDetailsScreen(order, navigatorNext),
                    clearAfterFirst = true
                )
            })
        }
    }

}
