package me.likeavitoapp.screens.main.order.create.payment


import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.checkLuhnAlgorithm
import me.likeavitoapp.develop
import me.likeavitoapp.isDigitsOnly
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.mainSet
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Worker
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.PaymentData
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.act
import me.likeavitoapp.model.expect
import me.likeavitoapp.model.check
import me.likeavitoapp.model.checkList
import me.likeavitoapp.model.useCase
import me.likeavitoapp.model.withTests

import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.order.details.OrderDetailsScreen


class PaymentScreen(
    val ad: Ad,
    val navigatorPrev: ScreensNavigator,
    val navigatorNext: ScreensNavigator,

    val scope: CoroutineScope = mainSet.provideCoroutineScope(),
    val sources: DataSources = mainSet.provideDataSources()
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

    var isChangeCardNumberTested = false
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

                    withTests(
                        enabled = develop && !isChangeCardNumberTested,
                        realInput = format(number),
                        outputMaker = { format(it) },
                        testCases = listOf(
                            "".expect(false),
                            "abc".expect(false),
                            "1".expect(false),
                            "1111".expect(false),
                            "1111111111111111".expect(false),
                            "5580 4733 7202 4733".expect(true),
                            "5580473372024".expect(false),
                            "558047337202".expect(false),
                            "55804733".expect(false),
                            "5580".expect(false),
                            "4026843483168683".expect(true),
                            "4026 8434 83168683".expect(true),
                            "2730 1684 6416 1841".expect(true),
                            "1111 1111 1111 1111 2".expect(false),
                            "1111 1111 1111 112".expect(false),
                            "1111 1111 1111 112w".expect(false),
                            "1111 1111/1111 1123".expect(false)
                        )
                    ) { output ->
                        isChangeCardNumberTested = true

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

    var isChangeMmYyTested = false
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

                    withTests(
                        enabled = develop && !isChangeMmYyTested,
                        realInput = format(text),
                        outputMaker = { format(it) },
                        testCases = listOf(
                            "".expect(false),
                            "1226".expect(true),
                            "122".expect(false),
                            "12222".expect(false),
                            "122".expect(false),
                            "0122".expect(true),
                            "0022".expect(false),
                            "0922".expect(true),
                            "1222".expect(true),
                            "1200".expect(true),
                            "1322".expect(false),
                            "22".expect(false),
                            "/".expect(false),
                            "abc".expect(false),
                            "aabb".expect(false)
                        ),
                    ) { output ->
                        isChangeMmYyTested = true

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

    var isChangeCvvCvcTested = false
    fun ChangeCvvCvcUseCase(text: String) {
        recordScenarioStep(text)

        useCase("Change cvv/cvc", text)
            .expect("should to show cvv in format: 123") {
                if (text.length > "123".length) {
                    return@expect
                }

                state.paymentData.cvvCvc.worker().act {
                    withTests(
                        enabled = develop && !isChangeCvvCvcTested,
                        realInput = text,
                        testCases = listOf(
                            "".expect(false),
                            "abc".expect(false),
                            "12d".expect(false),
                            "123".expect(true),
                            "12".expect(false),
                            "1234".expect(false),
                            "12%".expect(false),
                            " 12".expect(false)
                        )
                    ) { output ->
                        isChangeCvvCvcTested = true

                        checkList(
                            check { output.length == 3 },
                            check { output.isDigitsOnly() }
                        )
                    }
                }
            }
    }
}
