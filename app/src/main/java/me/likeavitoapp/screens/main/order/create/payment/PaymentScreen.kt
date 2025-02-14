package me.likeavitoapp.screens.main.order.create.payment


import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.isDigitsOnly
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Worker
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.PaymentData
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.TestCase
import me.likeavitoapp.model.act
import me.likeavitoapp.model.check
import me.likeavitoapp.model.checkList
import me.likeavitoapp.model.expect
import me.likeavitoapp.model.useCase
import me.likeavitoapp.model.withTests
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
        val payment = Worker<Order?>(null)
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
                state.payment.output.post(order)
                navigatorNext.startScreen(
                    screen = OrderDetailsScreen(order, navigatorNext),
                    clearAfterFirst = true
                )
            })
        }
    }

    fun ChangeCardNumberUseCase(number: String) {
        recordScenarioStep(number)

        useCase("Change card number", number)
            .expect("should to show card number in format: 1111 1111 1111 1111") {
                state.paymentData.cardNumber.worker().act {
                    val output = with(number.replace(" ", "")) {
                        val chunked = it.chunked(4)
                        var result = ""
                        chunked.forEachIndexed { i, item ->
                            result += if (i == 0) {
                                chunked[i]
                            } else {
                                " ${chunked[i]}"
                            }
                        }
                        return@with result
                    }

                    withTests(
                        realOutput = output,
                        testOutputs = listOf(
                            TestCase("", false),
                            TestCase("1", false),
                            TestCase("1111", false),
                            TestCase("1111 1111 1111 1111", false),
                            TestCase("5580 4733 7202 4733", true),
                            TestCase("4026 8434 8316 8683", true),
                            TestCase("4026843483168683", false),
                            TestCase("4026 8434 83168683", false),
                            TestCase("2730 1684 6416 1841", true),
                            TestCase("1111 1111 1111 1111 2", false),
                            TestCase("1111 1111 1111 112", false),
                            TestCase("1111 1111 1111 112w", false),
                            TestCase("1111 1111/1111 1123", false)
                        )
                    ) { output ->
                        checkList(
                            check { output.length == 19 },
                            check {
                                val digits = output.replace(" ", "")
                                digits.length == 16
                                        && digits.isDigitsOnly()
                                        && checkLuhnAlgorithm(digits)
                            }
                        )
                    }
                }
            }
    }

    fun ChangeMmYyUseCase(text: String) {
        recordScenarioStep(text)
    }

    fun ChangeCvvCvcUseCase(text: String) {
        recordScenarioStep(text)
    }

    fun checkLuhnAlgorithm(digits: String): Boolean {
        // Проверяем, что номер состоит только из цифр и имеет длину от 13 до 19
        if (digits.length < 13 || digits.length > 19) {
            return false
        }

        // Применяем алгоритм Луна
        var sum = 0
        val shouldDouble = digits.length % 2 == 0

        for (i in digits.indices) {
            var digit = digits[i].digitToInt()

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
