package me.likeavitoapp.screens.main.tabs.cart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.ScreensNavigator


class CartScreen(val navigator: ScreensNavigator) : IScreen {

    val state = State()

    enum class Tabs {
        New,
        Active,
        Archived
    }

    class State {
        var navHistory = mutableListOf<Tabs>()
        var selectedTab by mutableStateOf(Tabs.New)

        val activeOrders by mutableStateOf(emptyList<Order>())
        val archivedOrders by mutableStateOf(emptyList<Order>())
        val orders by mutableStateOf(emptyList<Order>())
    }

    fun LoadDataUseCase() {}

    fun SelectTabUseCase(tab: Tabs) {
        state.selectedTab = tab
        state.navHistory.add(tab)
    }

    lateinit var tabsNavigator : ScreensNavigator
}