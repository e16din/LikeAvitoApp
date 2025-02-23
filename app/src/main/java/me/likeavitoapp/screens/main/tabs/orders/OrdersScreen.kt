package me.likeavitoapp.screens.main.tabs.orders

import android.content.Context
import me.likeavitoapp.get
import me.likeavitoapp.launchCustomTabs
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.Worker
import me.likeavitoapp.model.act
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.addetails.AdDetailsScreen
import me.likeavitoapp.screens.main.tabs.chat.ChatScreen


class OrdersScreen(val navigator: ScreensNavigator) : IScreen {

    class State {
        val activeOrders = Worker<List<Order>>(emptyList())
        val tabIndex = UpdatableState<Int>(0)
        val archivedOrders = Worker<List<Order>>(emptyList())
    }

    val state = State()

    fun StartScreenUseCase() {
        recordScenarioStep()

        state.activeOrders.act {
            val result = get.sources().backend.orderService.getActiveOrders()

            result.getOrNull()?.let { orders ->
                orders.forEach { order ->
                    order.ad.newMessagesCount.act {
                        val result = get.sources().backend.adsService.getNewMessagesCount()
                        return@act Pair(result.getOrNull() ?: 0, result.isSuccess)
                    }
                }
            }


            return@act Pair(result.getOrNull() ?: emptyList(), result.isSuccess)
        }

        state.archivedOrders.act {
            val result = get.sources().backend.orderService.getArchivedOrders()
            return@act Pair(result.getOrNull() ?: emptyList(), result.isSuccess)
        }
    }

    fun ClickToAddressUseCase(address: String, context: Context) {
        recordScenarioStep(address)

        context.launchCustomTabs("https://yandex.ru/maps/?text=$address")
    }

    fun ClickToAdUseCase(order: Order) {
        recordScenarioStep(order)

        navigator.startScreen(
            AdDetailsScreen(order.ad, navigator)
        )
    }

    fun ClickToMessagesUseCase(order: Order) {
        recordScenarioStep(order)

        navigator.startScreen(
            ChatScreen(order.ad, navigator)
        )
    }

    fun ClickToTabUseCase(tabIndex: Int) {
        recordScenarioStep(tabIndex)

        state.tabIndex.post(tabIndex)
    }
}