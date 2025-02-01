package me.likeavitoapp.screens.main.tabs.cart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.dataSources


class CartScreen(
    val sources: DataSources = dataSources(),
) : IScreen {

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

    lateinit var navigator : ScreensNavigator
}