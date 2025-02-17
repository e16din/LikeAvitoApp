package me.likeavitoapp.screens.main.order.create.payment


import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import me.likeavitoapp.checkLuhnAlgorithm
import me.likeavitoapp.develop
import me.likeavitoapp.format
import me.likeavitoapp.isDigitsOnly
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.log
import me.likeavitoapp.get
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.Worker
import me.likeavitoapp.model.act
import me.likeavitoapp.model.check
import me.likeavitoapp.model.checkList
import me.likeavitoapp.model.expectIsValid
import me.likeavitoapp.model.withTests
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.order.details.OrderDetailsScreen


class PaymentScreen(
    val ad: Ad,
    val navigator: ScreensNavigator
) : IScreen {

    class State() {
        val payment = Worker(Unit)
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

        fun reformat(value: TextFieldValue): TextFieldValue {
            val cursor = '|'
            val delimiter = ' '
            var result = format(
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

        state.cardNumber.worker().act {
            withTests(
                realInput = value,
                outputMaker = { reformat(it) },
                testsEnabled = develop && !isChangeCardNumberTested,
                testCases = listOf(
                    TextFieldValue("") expectIsValid false,
                    TextFieldValue("abc") expectIsValid false,
                    TextFieldValue("1") expectIsValid false,
                    TextFieldValue("1111") expectIsValid false,
                    TextFieldValue("1111111111111111") expectIsValid false,
                    TextFieldValue("5580 4733 7202 4733") expectIsValid true,
                    TextFieldValue("5580473372024") expectIsValid false,
                    TextFieldValue("558047337202") expectIsValid false,
                    TextFieldValue("55804733") expectIsValid false,
                    TextFieldValue("5580") expectIsValid false,
                    TextFieldValue("4026843483168683") expectIsValid true,
                    TextFieldValue("4026 8434 83168683") expectIsValid true,
                    TextFieldValue("2730 1684 6416 1841") expectIsValid true,
                    TextFieldValue("1111 1111 1111 1111 2") expectIsValid false,
                    TextFieldValue("1111 1111 1111 112") expectIsValid false,
                    TextFieldValue("1111 1111 1111 112w") expectIsValid false,
                    TextFieldValue("1111 1111/1111 1123") expectIsValid false
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
                }
            )
        }
    }

    private val stringBuilder = StringBuilder()

    private var isChangeMmYyTested = false
    fun ChangeMmYyUseCase(value: TextFieldValue, removeOneChar: Boolean) {
        recordScenarioStep(value)

        log("Change MM/YY use case: ${value.text}")
        log("should to show month and year in format: mm/yy")

        fun reformat(value: TextFieldValue): TextFieldValue {
            val cursor = '|'
            val delimiter = '/'
            var result = format(
                text = stringBuilder.append(value.text)
                    .insert(value.selection.start, cursor).toString(),
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

        state.mmYy.worker().act {
            withTests(
                realInput = value,
                outputMaker = { reformat(it) },
                testsEnabled = develop && !isChangeMmYyTested,
                testCases = listOf(
                    TextFieldValue("") expectIsValid false,
                    TextFieldValue("1234") expectIsValid true,
                    TextFieldValue("122") expectIsValid false,
                    TextFieldValue("12222") expectIsValid false,
                    TextFieldValue("122") expectIsValid false,
                    TextFieldValue("0122") expectIsValid true,
                    TextFieldValue("0022") expectIsValid false,
                    TextFieldValue("0922") expectIsValid true,
                    TextFieldValue("1222") expectIsValid true,
                    TextFieldValue("1200") expectIsValid true,
                    TextFieldValue("1322") expectIsValid false,
                    TextFieldValue("22") expectIsValid false,
                    TextFieldValue("/") expectIsValid false,
                    TextFieldValue("abc") expectIsValid false,
                    TextFieldValue("aabb") expectIsValid false
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
                }
            )
        }
    }

    // NOTE: мэпь,редьюзь данные в том месте где они используются,
    // тогда меньше придется переписывать код

    var isChangeCvvCvcTested = false
    fun ChangeCvvCvcUseCase(value: TextFieldValue) {
        recordScenarioStep(value.text)

        log("Change cvv/cvc use case: ${value.text}")
        log("should to show cvv in format: 123")

        state.cvvCvc.worker().act {
            withTests(
                realInput = value,
                testsEnabled = develop && !isChangeCvvCvcTested,
                testCases = listOf(
                    TextFieldValue("") expectIsValid false,
                    TextFieldValue("abc") expectIsValid false,
                    TextFieldValue("12d") expectIsValid false,
                    TextFieldValue("123") expectIsValid true,
                    TextFieldValue("12") expectIsValid false,
                    TextFieldValue("1234") expectIsValid false,
                    TextFieldValue("12%") expectIsValid false,
                    TextFieldValue(" 12") expectIsValid false
                ),
                validator = { output ->
                    isChangeCvvCvcTested = true

                    checkList(
                        check { output.text.length == 3 },
                        check { output.text.isDigitsOnly() }
                    )
                }
            )
        }
    }
}
