package me.likeavitoapp

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.likeavitoapp.model.check
import me.likeavitoapp.model.checkList
import me.likeavitoapp.model.expectIsValid
import me.likeavitoapp.model.expectOutput
import me.likeavitoapp.model.mockMainSet


import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.model.withTests
import me.likeavitoapp.screens.main.order.create.payment.PaymentScreen
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PaymentTests {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        get = mockMainSet().apply {
            defaultContext = testDispatcher
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // сбросьте диспетчер после теста
//        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `check ChangeCardNumberUseCase`() {
        val screen = mockScreen()
        screen.ChangeCardNumberUseCase(TextFieldValue("", TextRange(0)), false)
    }

    @Test
    fun `check ChangeMmYyUseCase`() {
        val screen = mockScreen()
        screen.ChangeMmYyUseCase(TextFieldValue("", TextRange(0)), false)
    }

    @Test
    fun `check ChangeCvvCvcUseCase`() {
        val screen = mockScreen()
        screen.ChangeCvvCvcUseCase(TextFieldValue("", TextRange(0)))
    }

    private fun mockScreen(): PaymentScreen = PaymentScreen(
        navigator = mockScreensNavigator(),
        ad = MockDataProvider().ads.first()
    )

    @Test
    fun `check format(isRemove = false)`(){
        val delimiter = '/'
        val cursor = '|'
        val mask = "##/##"

        fun test(value: String): String {
            var result = format(
                text = value.toString(),
                mask = mask,
                cursor = cursor,
                delimiter = delimiter,
                removeOneChar = false
            )

            return result
        }

        withTests(
            realInput = "1234|",
            outputMaker = { test(it) },
            testsEnabled = true,
            withAssert = true,
            testCases = listOf(
                "" expectOutput "|",
                "|" expectOutput "|",
                "1234|" expectOutput "12/34|",
                "123|4" expectOutput "12/3|4",
                "12|34" expectOutput "12/|34",
                "1|234" expectOutput "1|2/34",
                "|1234" expectOutput "|12/34",
                "12|3" expectOutput "12/|3",
                "123|" expectOutput "12/3|",
                "|123" expectOutput "|12/3",
                "|12" expectOutput "|12",
                "12|" expectOutput "12/|",
                "1|" expectOutput "1|",
                "|1" expectOutput "|1",
            )
        )
    }

    @Test
    fun `check format(isRemove = true)`(){
        val delimiter = '/'
        val cursor = '|'
        val mask = "##/##"

        fun test(value: String): String {
            var result = format(
                text = value.toString(),
                mask = mask,
                cursor = cursor,
                delimiter = delimiter,
                removeOneChar = true
            )

            return result
        }

        withTests(
            realInput = "1234|",
            outputMaker = { test(it) },
            testsEnabled = true,
            withAssert = true,
            testCases = listOf(
                "" expectOutput "|",
                "|" expectOutput "|",
                "1234|" expectOutput "12/3|",
                "123|4" expectOutput "12/|4",
                "12|34" expectOutput "1|3/4",
                "1|234" expectOutput "|23/4",
                "|1234" expectOutput "|12/34",
                "12|3" expectOutput "1|3",
                "123|" expectOutput "12/|",
                "|123" expectOutput "|12/3",
                "|12" expectOutput "|12",
                "12|" expectOutput "1|",
                "1|" expectOutput "|",
                "|1" expectOutput "|1",
            )
        )
    }
}