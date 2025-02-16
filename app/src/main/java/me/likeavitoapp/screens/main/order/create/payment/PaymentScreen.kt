package me.likeavitoapp.screens.main.order.create.payment


import androidx.compose.ui.text.input.TextFieldValue
import me.likeavitoapp.checkLuhnAlgorithm
import me.likeavitoapp.develop
import me.likeavitoapp.isDigitsOnly
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.log
import me.likeavitoapp.get
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.Worker
import me.likeavitoapp.model.act
import me.likeavitoapp.model.check
import me.likeavitoapp.model.checkList
import me.likeavitoapp.model.expect
import me.likeavitoapp.model.withTests
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.order.details.OrderDetailsScreen


class PaymentScreen(
    val ad: Ad,
    val navigator: ScreensNavigator
) : IScreen {

    class State() {
        val payment = Worker<Order?>(null)
        val cardNumber = Worker(TextFieldValue("")) // 1111 1111 1111 1111
        val mmYy = Worker(TextFieldValue("")) // mm/yy
        val cvvCvc = Worker(TextFieldValue("")) // 123
    }

    val state = State()

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ClickToCloseUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ClickToDoneUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ClickToPayUseCase() {
        recordScenarioStep()

        get.scope().launchWithHandler {
            state.payment.load(loading = {
                return@load get.sources().backend.orderService.pay(
                    ad = ad,
                    cardNumber = state.cardNumber.data().text,
                    mmYy = state.mmYy.data().text,
                    cvvCvc = state.cvvCvc.data().text,
                )
            }, onSuccess = { order ->
                state.payment.output.post(order)
                navigator.startScreen(
                    screen = OrderDetailsScreen(order, navigator),
                    clearAfterFirst = true
                )
            })
        }
    }

    var isChangeCardNumberTested = false
    fun ChangeCardNumberUseCase(value: TextFieldValue) {
        recordScenarioStep(value.text)

        log("Change card number use case: ${value}")
        log("should to show card number in format: 1111 1111 1111 1111")

        state.cardNumber.worker().act {
            fun format(value: TextFieldValue): TextFieldValue {
                val chunked = value.text.replace(" ", "").chunked(4)
                var result = ""
                chunked.forEachIndexed { i, item ->
                    result += if (i == 0) {
                        chunked[i]
                    } else {
                        " ${chunked[i]}"
                    }
                }
                return return TextFieldValue(result)
            }

            withTests(
                enabled = develop && !isChangeCardNumberTested,
                realInput = format(value),
                outputMaker = { format(it) },
                testCases = listOf(
                    TextFieldValue("") expect false,
                    TextFieldValue("abc") expect false,
                    TextFieldValue("1") expect false,
                    TextFieldValue("1111") expect false,
                    TextFieldValue("1111111111111111") expect false,
                    TextFieldValue("5580 4733 7202 4733") expect true,
                    TextFieldValue("5580473372024") expect false,
                    TextFieldValue("558047337202") expect false,
                    TextFieldValue("55804733") expect false,
                    TextFieldValue("5580") expect false,
                    TextFieldValue("4026843483168683") expect true,
                    TextFieldValue("4026 8434 83168683") expect true,
                    TextFieldValue("2730 1684 6416 1841") expect true,
                    TextFieldValue("1111 1111 1111 1111 2") expect false,
                    TextFieldValue("1111 1111 1111 112") expect false,
                    TextFieldValue("1111 1111 1111 112w") expect false,
                    TextFieldValue("1111 1111/1111 1123") expect false
                )
            ) { output ->
                isChangeCardNumberTested = true

                checkList(
                    check { output.text.length == 19 },
                    check {
                        val digits = output.text.replace(" ", "")
                        digits.length == 16
                                && digits.isDigitsOnly()
                                && checkLuhnAlgorithm(digits)
                    }
                )
            }
        }
    }

    var isChangeMmYyTested = false
    fun ChangeMmYyUseCase(value: TextFieldValue) {
        recordScenarioStep(value)

        log("Change MM/YY use case: ${value.text}")
        log("should to show month and year in format: mm/yy")

        state.mmYy.worker().act {
            val isRemoving = value.text.length < state.mmYy.data().text.length
            if (isRemoving) {
                // && | position == / position, remove "/"
            } else {
                // && | position == / position, | position += 1
            }
            fun format(value: TextFieldValue): TextFieldValue {
                var result = ""
                val chunked = value.text.replace("/", "").chunked(2)
                if (!chunked.isEmpty()) {
                    result += chunked[0] + "/"
                }
                if (chunked.size == 2) {
                    result += chunked[1]
                }

                return TextFieldValue(result)
            }

            withTests(
                enabled = develop && !isChangeMmYyTested,
                realInput = format(value),
                outputMaker = { format(it) },
                testCases = listOf(
                    TextFieldValue("") expect false,
                    TextFieldValue("1226") expect true,
                    TextFieldValue("122") expect false,
                    TextFieldValue("12222") expect false,
                    TextFieldValue("122") expect false,
                    TextFieldValue("0122") expect true,
                    TextFieldValue("0022") expect false,
                    TextFieldValue("0922") expect true,
                    TextFieldValue("1222") expect true,
                    TextFieldValue("1200") expect true,
                    TextFieldValue("1322") expect false,
                    TextFieldValue("22") expect false,
                    TextFieldValue("/") expect false,
                    TextFieldValue("abc") expect false,
                    TextFieldValue("aabb") expect false
                ),
            ) { output ->
                isChangeMmYyTested = true

                val parts = output.text.split("/")
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

    // NOTE: мэпь,редьюзь данные в том месте где они используются, тогда меньше придется переписывать код

    var isChangeCvvCvcTested = false
    fun ChangeCvvCvcUseCase(value: TextFieldValue) {
        recordScenarioStep(value.text)

        log("Change cvv/cvc use case: ${value.text}")
        log("should to show cvv in format: 123")

        state.cvvCvc.worker().act {
            withTests(
                enabled = develop && !isChangeCvvCvcTested,
                realInput = value,
                testCases = listOf(
                    TextFieldValue("") expect false,
                    TextFieldValue("abc") expect false,
                    TextFieldValue("12d") expect false,
                    TextFieldValue("123") expect true,
                    TextFieldValue("12") expect false,
                    TextFieldValue("1234") expect false,
                    TextFieldValue("12%") expect false,
                    TextFieldValue(" 12") expect false
                )
            ) { output ->
                isChangeCvvCvcTested = true

                checkList(
                    check { output.text.length == 3 },
                    check { output.text.isDigitsOnly() }
                )
            }
        }
    }
}
