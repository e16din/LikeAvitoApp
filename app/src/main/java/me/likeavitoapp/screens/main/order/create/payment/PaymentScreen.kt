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

    fun ChangeCardNumberUseCase(text: String) {
        recordScenarioStep(text)
    }

    fun ChangeMmYyUseCase(text: String) {
        recordScenarioStep(text)
    }

    fun ChangeCvvCvcUseCase(text: String) {
        recordScenarioStep(text)
    }

    fun isValidCardNumber(cardNumber: String): Boolean {
        // Удаляем все пробелы и дефисы из номера карты
        val cleanedNumber = cardNumber.replace(Regex("[^\\d]"), "")

        // Проверяем, что номер состоит только из цифр и имеет длину от 13 до 19
        if (cleanedNumber.length < 13 || cleanedNumber.length > 19) {
            return false
        }

        // Применяем алгоритм Луна
        var sum = 0
        val shouldDouble = cleanedNumber.length % 2 == 0

        for (i in cleanedNumber.indices) {
            var digit = cleanedNumber[i].digitToInt()

            // Удваиваем каждую вторую цифру
            if ((i % 2 == 0 && shouldDouble) || (i % 2 != 0 && !shouldDouble)) {
                digit *= 2
                // Если результат больше 9, вычитаем 9
                if (digit > 9) {
                    digit -= 9
                }
            }
            sum += digit
        }

        // Проверяем, делится ли сумма на 10 без остатка
        return sum % 10 == 0
    }
}
