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
                        realOutput = output,
                        testCases = listOf(
                            TestCase(format(""), false),
                            TestCase(format("abc"), false),
                            TestCase(format("1"), false),
                            TestCase(format("1111"), false),
                            TestCase(format("1111111111111111"), false),
                            TestCase(format("5580 4733 7202 4733"), true),
                            TestCase(format("4026843483168683"), true),
                            TestCase(format("4026 8434 83168683"), true),
                            TestCase(format("2730 1684 6416 1841"), true),
                            TestCase(format("1111 1111 1111 1111 2"), false),
                            TestCase(format("1111 1111 1111 112"), false),
                            TestCase(format("1111 1111 1111 112w"), false),
                            TestCase(format("1111 1111/1111 1123"), false)
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
                state.paymentData.mmYy.act {
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
                        realOutput = output,
                        testCases = listOf(
                            TestCase(format(""), false),
                            TestCase(format("1226"), true),
                            TestCase(format("122"), false),
                            TestCase(format("12222"), false),
                            TestCase(format("122"), false),
                            TestCase(format("0122"), true),
                            TestCase(format("0022"), false),
                            TestCase(format("0922"), true),
                            TestCase(format("1222"), true),
                            TestCase(format("1200"), true),
                            TestCase(format("1322"), false),
                            TestCase(format("22"), false),
                            TestCase(format("/"), false),
                            TestCase(format("abc"), false),
                            TestCase(format("aabb"), false)
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
                state.paymentData.cvvCvc.worker().act {
                    withTests(
                        realOutput = text,
                        testCases = listOf(
                            TestCase("", false),
                            TestCase("abc", false),
                            TestCase("12d", false),
                            TestCase("123", true),
                            TestCase("12", false),
                            TestCase("1234", false),
                            TestCase("12%", false),
                            TestCase(" 12", false),
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
