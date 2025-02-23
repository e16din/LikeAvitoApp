package me.likeavitoapp


import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.likeavitoapp.mocks.MockDataProvider
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
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
        Dispatchers.resetMain()
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
        ad = MockDataProvider().ads.first(),
        orderType = Order.Type.Pickup
    )

}