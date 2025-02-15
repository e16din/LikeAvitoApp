package me.likeavitoapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
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
        mainSet = mockMainSet().apply {
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
        screen.ChangeCardNumberUseCase("")
    }

    @Test
    fun `check ChangeMmYyUseCase`() {
        val screen = mockScreen()
        screen.ChangeMmYyUseCase("")
    }

    @Test
    fun `check ChangeCvvCvcUseCase`() {
        val screen = mockScreen()
        screen.ChangeCvvCvcUseCase("")
    }

    private fun mockScreen(): PaymentScreen = PaymentScreen(
        navigator = mockScreensNavigator(),
        navigator = mockScreensNavigator(),
        ad = MockDataProvider().ads.first()
    )
}