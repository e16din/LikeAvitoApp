package me.likeavitoapp.screens.main.order.create.payment


import androidx.compose.ui.text.TextRange
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
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.Worker
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
        val payment = Worker(Unit)
        val cardNumber = UpdatableState(TextFieldValue("")) // 1111 1111 1111 1111
        val mmYy = UpdatableState(TextFieldValue("")) // mm/yy
        val cvvCvc = UpdatableState(TextFieldValue("")) // 123
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
                    cardNumber = state.cardNumber.value.text,
                    mmYy = state.mmYy.value.text,
                    cvvCvc = state.cvvCvc.value.text,
                )
            }, onSuccess = { order ->
                state.payment.output.post(Unit)
                navigator.startScreen(
                    screen = OrderDetailsScreen(order, navigator),
                    clearAfterFirst = true
                )
            })
        }
    }

    var isChangeCardNumberTested = false
    fun ChangeCardNumberUseCase(value: TextFieldValue, removeOneChar: Boolean) {
        recordScenarioStep(value.text)

        log("Change card number use case: ${value}")
        log("should to show card number in format: 1111 1111 1111 1111")

        fun format(value: TextFieldValue): TextFieldValue {
            val cursor = '|'
            val delimiter = ' '
            var result = me.likeavitoapp.format(
                text = stringBuilder.append(value.text).insert(value.selection.start, cursor)
                    .toString(),
                mask = "#### #### #### ####",
                delimiter = delimiter,
                cursor = cursor,
                removeOneChar = removeOneChar,
                stringBuilder = stringBuilder
            )
            stringBuilder.clear()
            var newPosition = result.indexOf('|')
            result = result.replace("|", "")

            return TextFieldValue(result, TextRange(newPosition))
        }

        withTests(
            realInput = value,
            outputMaker = { format(it) },
            testsEnabled = develop && !isChangeCardNumberTested,
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
            ),
            validator = { output ->
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
            },
            onDone = { output ->
                state.cardNumber.post(output)
            }
        )
    }

    private val stringBuilder = StringBuilder()

    private var isChangeMmYyTested = false
    fun ChangeMmYyUseCase(value: TextFieldValue, removeOneChar:Boolean) {
        recordScenarioStep(value)

        log("Change MM/YY use case: ${value.text}")
        log("should to show month and year in format: mm/yy")

        fun format(value: TextFieldValue): TextFieldValue {
            val cursor = '|'
            val delimiter = '/'
            var result = me.likeavitoapp.format(
                text = stringBuilder.append(value.text).insert(value.selection.start, cursor)
                    .toString(),
                mask = "##/##",
                delimiter = delimiter,
                cursor = cursor,
                removeOneChar = removeOneChar,
                stringBuilder = stringBuilder
            )
            stringBuilder.clear()
            var newPosition = result.indexOf('|')
            result = result.replace("|", "")

            return TextFieldValue(result, TextRange(newPosition))
        }

        withTests(
            realInput = value,
            outputMaker = { format(it) },
            testsEnabled = develop && !isChangeMmYyTested,
            testCases = listOf(
                TextFieldValue("") expect false,
                TextFieldValue("1234") expect true,
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
            validator = { output ->
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
            },
            onDone = { output ->
                state.mmYy.post(output)
            }
        )
    }

    // NOTE: мэпь,редьюзь данные в том месте где они используются,
    // тогда меньше придется переписывать код

    var isChangeCvvCvcTested = false
    fun ChangeCvvCvcUseCase(value: TextFieldValue) {
        recordScenarioStep(value.text)

        log("Change cvv/cvc use case: ${value.text}")
        log("should to show cvv in format: 123")

        withTests(
            realInput = value,
            testsEnabled = develop && !isChangeCvvCvcTested,
            testCases = listOf(
                TextFieldValue("") expect false,
                TextFieldValue("abc") expect false,
                TextFieldValue("12d") expect false,
                TextFieldValue("123") expect true,
                TextFieldValue("12") expect false,
                TextFieldValue("1234") expect false,
                TextFieldValue("12%") expect false,
                TextFieldValue(" 12") expect false
            ),
            validator = { output ->
                isChangeCvvCvcTested = true

                checkList(
                    check { output.text.length == 3 },
                    check { output.text.isDigitsOnly() }
                )
            },
            onDone = { output ->
                state.cvvCvc.post(output)
            }
        )
    }
}
