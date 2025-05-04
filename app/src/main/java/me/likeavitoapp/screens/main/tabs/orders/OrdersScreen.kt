package me.likeavitoapp.screens.main.tabs.orders

import android.content.Context
import me.likeavitoapp.get
import me.likeavitoapp.launchCustomTabs
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.OwnAd
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.Worker
import me.likeavitoapp.model.load
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.addetails.AdDetailsScreen
import me.likeavitoapp.screens.main.createad.CreateAdScreen
import me.likeavitoapp.screens.main.tabs.chat.ChatScreen


class OrdersScreen(val navigator: ScreensNavigator) : IScreen {

    class State {
        val activeOrders = Worker<List<Order>>(emptyList())
        val archivedOrders = Worker<List<Order>>(emptyList())
        val ownAds = Worker<List<OwnAd>>(emptyList())
        val tabIndex = UpdatableState(0)
    }

    val state = State()

    fun StartScreenUseCase() {
        recordScenarioStep()

        state.activeOrders.load {
            val result = get.sources().backend.orderService.getActiveOrders()

            result.getOrNull()?.let { orders ->
                orders.forEach { order ->
                    order.ad.newMessagesCount.load {
                        val result = get.sources().backend.adsService.getNewMessagesCount(order.ad.id)
                        return@load Pair(result.getOrNull() ?: 0, result.isSuccess)
                    }
                }
            }

            return@load Pair(result.getOrNull() ?: emptyList(), result.isSuccess)
        }

        state.archivedOrders.load {
            val result = get.sources().backend.orderService.getArchivedOrders()
            return@load Pair(result.getOrNull() ?: emptyList(), result.isSuccess)
        }

        state.ownAds.load {
            val result = get.sources().backend.orderService.getOwnAds()

            result.getOrNull()?.let { ownAds ->
                ownAds.forEach { ownAd ->
                    ownAd.newMessagesCount.load {
                        val result = get.sources().backend.adsService.getNewMessagesCount(ownAd.id)
                        return@load Pair(result.getOrNull() ?: 0, result.isSuccess)
                    }
                }
            }

            return@load Pair(result.getOrNull() ?: emptyList(), result.isSuccess)
        }
    }

    fun ClickToAddressUseCase(address: String, context: Context) {
        recordScenarioStep(address)

        context.launchCustomTabs("https://yandex.ru/maps/?text=$address")
    }

    fun ClickToAdUseCase(order: Order) {
        recordScenarioStep(order)

        navigator.startScreen(
            AdDetailsScreen(order.ad, navigator),
        )
    }

    fun ClickToOrderMessagesUseCase(order: Order) {
        recordScenarioStep(order)

        navigator.startScreen(
            ChatScreen(order.ad.id, order.ad.title, navigator),
        )
    }

    fun ClickToOwnAdMessagesUseCase(ownAd: OwnAd) {
        recordScenarioStep(ownAd)

        navigator.startScreen(
            ChatScreen(ownAd.id, ownAd.title!!, navigator),
        )
    }

    fun ClickToTabUseCase(tabIndex: Int) {
        recordScenarioStep(tabIndex)

        state.tabIndex.next(tabIndex)
    }

    fun ClickToOwnAdUseCase(ownAd: OwnAd) {
        recordScenarioStep(ownAd)

        navigator.startScreen(
            CreateAdScreen(
                navigator = navigator,
                activeCreateAdRequest = ownAd
            )
        )
    }
}