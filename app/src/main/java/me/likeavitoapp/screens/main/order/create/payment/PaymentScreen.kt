package me.likeavitoapp.screens.main.order.create.payment


import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.checkLuhnAlgorithm
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
import me.likeavitoapp.model.act
import me.likeavitoapp.model.asTestCase
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
                if (number.length > "1111 1111 1111 1111".length) {
                    return@expect
                }

                state.paymentData.cardNumber.worker().act {
                    fun format(value: String): String {
                        val chunked = value.replace(" ", "").chunked(4)
                        var result = ""
                        chunked.forEachIndexed { i, item ->
                            result += if (i == 0) {
                                chunked[i]
                            } else {
                                " ${chunked[i]}"
                            }
                        }
                        return result
                    }

                    val output = format(number)

                    withTests(
                        realInput = output,
                        outputMaker = { format(it) },
                        testCases = listOf(
                            "".asTestCase(false),
                            "abc".asTestCase(false),
                            "1".asTestCase(false),
                            "1111".asTestCase(false),
                            "1111111111111111".asTestCase(false),
                            "5580 4733 7202 4733".asTestCase(true),
                            "5580473372024".asTestCase(false),
                            "558047337202".asTestCase(false),
                            "55804733".asTestCase(false),
                            "5580".asTestCase(false),
                            "4026843483168683".asTestCase(true),
                            "4026 8434 83168683".asTestCase(true),
                            "2730 1684 6416 1841".asTestCase(true),
                            "1111 1111 1111 1111 2".asTestCase(false),
                            "1111 1111 1111 112".asTestCase(false),
                            "1111 1111 1111 112w".asTestCase(false),
                            "1111 1111/1111 1123".asTestCase(false)
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

        useCase("Change MM/YY", text)
            .expect("should to show month and year in format: mm/yy") {
                if (text.length > "mm/yy".length) {
                    return@expect
                }

                state.paymentData.mmYy.worker().act {
                    fun format(value: String): String {
                        var result = ""
                        val chunked = value.replace("/", "").chunked(2)
                        if (!chunked.isEmpty()) {
                            result += chunked[0] + "/"
                        }
                        if (chunked.size == 2) {
                            result += chunked[1]
                        }

                        return result
                    }

                    val output = format(text)

                    withTests(
                        realInput = output,
                        outputMaker = { format(it) },
                        testCases = listOf(
                            "".asTestCase(false),
                            "1226".asTestCase(true),
                            "122".asTestCase(false),
                            "12222".asTestCase(false),
                            "122".asTestCase(false),
                            "0122".asTestCase(true),
                            "0022".asTestCase(false),
                            "0922".asTestCase(true),
                            "1222".asTestCase(true),
                            "1200".asTestCase(true),
                            "1322".asTestCase(false),
                            "22".asTestCase(false),
                            "/".asTestCase(false),
                            "abc".asTestCase(false),
                            "aabb".asTestCase(false)
                        ),
                    ) { output ->
                        val parts = output.split("/")
                        val mm = parts[0]
                        val yy = parts.getOrNull(1)

                        checkList(
                            check { mm.length == 2 },
                            check { mm.isDigitsOnly() },
                            check {
                                val mmInt = mm.toInt()
                                mmInt > 0 && mmInt <= 12
                            },
                            check { yy?.length == 2 },
                            check { yy?.isDigitsOnly() == true },
                            check {
                                val yyInt = yy!!.toInt()
                                yyInt >= 0 && yyInt <= 99
                            }
                        )
                    }
                }
            }
    }

    fun ChangeCvvCvcUseCase(text: String) {
        recordScenarioStep(text)

        useCase("Change cvv/cvc", text)
            .expect("should to show cvv in format: 123") {
                if (text.length > "123".length) {
                    return@expect
                }

                state.paymentData.cvvCvc.worker().act {
                    withTests(
                        realInput = text,
                        testCases = listOf(
                            "".asTestCase(false),
                            "abc".asTestCase(false),
                            "12d".asTestCase(false),
                            "123".asTestCase(true),
                            "12".asTestCase(false),
                            "1234".asTestCase(false),
                            "12%".asTestCase(false),
                            " 12".asTestCase(false)
                        )
                    ) { output ->
                        checkList(
                            check { output.length == 3 },
                            check { output.isDigitsOnly() }
                        )
                    }
                }
            }
    }
}
