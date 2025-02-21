package me.likeavitoapp.screens.main.order.create.payment


import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import me.likeavitoapp.checkLuhnAlgorithm
import me.likeavitoapp.develop
import me.likeavitoapp.format
import me.likeavitoapp.get
import me.likeavitoapp.isDigitsOnly
import me.likeavitoapp.log
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.Worker
import me.likeavitoapp.model.act
import me.likeavitoapp.model.check
import me.likeavitoapp.model.expectIsValid
import me.likeavitoapp.model.testAll
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.order.details.OrderDetailsScreen


class PaymentScreen(
    val ad: Ad,
    val orderType: Order.Type,
    val navigator: ScreensNavigator
) : IScreen {

    class State() {
        val payment = Worker(Unit)
        val validationEnabled = UpdatableState(false)
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

        val hasInvalidValue = listOf(state.cardNumber, state.cvvCvc, state.mmYy)
            .any {
                println("it.data().text: ${it.data().text}")
                it.data().text.isEmpty() || it.hasFail()
            }
        if (hasInvalidValue) {
            state.validationEnabled.post(true)
            return
        }

        state.validationEnabled.post(false)

        state.payment.worker().act {
            val result = get.sources().backend.orderService.order(
                adId = ad.id,
                type = orderType,
                cardNumber = state.cardNumber.data().text,
                mmYy = state.mmYy.data().text,
                cvvCvc = state.cvvCvc.data().text,
            )

            val order = result.getOrNull()
            val isSuccess = order != null
            return@act Pair(Unit, isSuccess).also {
                if (isSuccess) {
                    navigator.startScreen(
                        screen = OrderDetailsScreen(order, navigator),
                        clearAfterFirst = true
                    )
                }
            }
        }
    }

    fun reformat(value: TextFieldValue, mask: String, delimiter:Char = ' ', removeOneChar: Boolean): TextFieldValue {
        val cursor = '|'
        var result = format(
            text = stringBuilder.append(value.text).insert(value.selection.start, cursor)
                .toString(),
            mask = mask,
            delimiter = delimiter,
            cursor = cursor,
            removeOneChar = removeOneChar,
            stringBuilder = stringBuilder
        )
        stringBuilder.clear()
        var newPosition = result.indexOf(cursor)
        result = result.replace("$cursor", "")

        return TextFieldValue(result, TextRange(newPosition))
    }

    fun ChangeCardNumberUseCase(value: TextFieldValue, removeOneChar: Boolean) {
        recordScenarioStep(value.text)

        log("Change card number use case: ${value}")
        log("should to show card number in format: 1111 1111 1111 1111")

        fun formatCardNumber(value: TextFieldValue): TextFieldValue {
            return reformat(value, "#### #### #### ####", ' ', removeOneChar)
        }

        fun validate(output: TextFieldValue): Boolean {
            return check(1) { output.text.length == 19 } &&
                    check(2) {
                        val digits = output.text.replace(" ", "")
                        digits.length == 16
                                && digits.isDigitsOnly()
                                && checkLuhnAlgorithm(digits)
                    }
        }

        state.cardNumber.worker(doOnce = {
            if (develop) {
                testAll(
                    outputMaker = { formatCardNumber(it) },
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
                        validate(output)
                    }
                )
            }
        }).act {
            val output = formatCardNumber(value)
            val isValid = validate(output)
            return@act Pair(output, isValid)
        }
    }

    private val stringBuilder = StringBuilder()

    fun ChangeMmYyUseCase(value: TextFieldValue, removeOneChar: Boolean) {
        recordScenarioStep(value)

        log("Change MM/YY use case: ${value.text}")
        log("should to show month and year in format: mm/yy")

        fun formatMmYy(value: TextFieldValue): TextFieldValue {
            return reformat(value, "##/##", '/', removeOneChar)
        }

        fun validate(output: TextFieldValue): Boolean {
            val parts = output.text.split("/")
            val mm = parts[0]
            val yy = parts.getOrNull(1)

            return check(1) { mm.length == 2 } &&
                    check(2) { mm.isDigitsOnly() } &&
                    check(3) {
                        val mmInt = mm.toInt()
                        mmInt > 0 && mmInt <= 12
                    } &&
                    check(4) { yy?.length == 2 } &&
                    check(5) { yy?.isDigitsOnly() == true } &&
                    check(6) {
                        val yyInt = yy!!.toInt()
                        yyInt >= 0 && yyInt <= 99
                    }
        }

        state.mmYy.worker(doOnce = {
            if (develop) {
                testAll(
                    outputMaker = { formatMmYy(it) },
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
                        validate(output)
                    }
                )
            }
        }).act {
            val output = formatMmYy(value)
            val isValid = validate(output)
            return@act Pair(output, isValid)
        }
    }

    // NOTE: мэпь,редьюзь данные в том месте где они используются,
    // тогда меньше придется переписывать код

    fun ChangeCvvCvcUseCase(value: TextFieldValue) {
        recordScenarioStep(value.text)

        log("Change cvv/cvc use case: ${value.text}")
        log("should to show cvv in format: 123")

        fun formatCvc(value: TextFieldValue): TextFieldValue {
            return reformat(value, "###", removeOneChar = false)
        }

        fun validate(output: TextFieldValue): Boolean {
            return check(1) { output.text.length == 3 } &&
                    check(2) { output.text.isDigitsOnly() }
        }

        state.cvvCvc.worker(doOnce = {
            if (develop) {
                testAll(
                    outputMaker = { formatCvc(it) },
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
                        validate(output)
                    }
                )
            }
        }).act {
            val output = formatCvc(value)
            val isValid = validate(output)
            return@act Pair(output, isValid)
        }
    }
}