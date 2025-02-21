package me.likeavitoapp.screens.main.tabs.cart

import me.likeavitoapp.get
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.Worker
import me.likeavitoapp.model.act


class OrdersScreen(val navigator: ScreensNavigator) : IScreen {

    enum class Tabs {
        ActiveTab,
        ArchivedTab
    }

    class State {
        val activeOrders = Worker<List<Order>>(emptyList())
        val archivedOrders = Worker<List<Order>>(emptyList())
    }

    val state = State()

    fun StartScreenUseCase() {
        state.activeOrders.act {
            val result = get.sources().backend.orderService.getActiveOrders()
            return@act Pair(result.getOrNull() ?: emptyList(), result.isSuccess)
        }
    }

    fun SelectTabUseCase(tab: Tabs) {

    }

}