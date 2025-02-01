package me.likeavitoapp.screens.main.addetails

import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.inverse
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IMessage
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.dataSources
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.order.create.CreateOrderScreen
import me.likeavitoapp.screens.main.tabs.chat.ChatScreen


class AdDetailsScreen(
    ad: Ad,
    val parentNavigator: ScreensNavigator,
    val sources: DataSources = dataSources(),
) : IScreen {

    val state: State = State(ad)
    val nav = Navigation(this)

    private val buyCalls = MutableStateFlow(Unit)

    suspend fun listenBuyCalls() {
        buyCalls.collect {
            state.reserve.loading.value = true
            val result = sources.backend.cartService.reserve(adId = state.ad.id)
            state.reserve.loading.value = false

            val isReserved = result.getOrNull()
            if (isReserved == true) {
                state.reserve.data.value = isReserved
                sources.app.navigator.startScreen(nav.stack.orderScreen())
            } else {
                state.reserve.loadingFailed.value = true
            }
        }
    }

    class State(
        val ad: Ad,
        val messages: MutableStateFlow<List<IMessage>> = MutableStateFlow(emptyList()),
        val reserve: Loadable<Boolean> = Loadable(false)
    )

    class Navigation(val screen: AdDetailsScreen, val stack: Stack = Stack(screen)) {
        class Stack(val screen: AdDetailsScreen) {
            fun orderScreen() = CreateOrderScreen(ad = screen.state.ad)
            fun chatScreen() = ChatScreen(ad = screen.state.ad)
        }
    }

    fun ClickToFavoriteUseCase() {
        recordScenarioStep()

        state.ad.isFavorite.inverse()
    }

    fun ClickToBuyUseCase() {
        recordScenarioStep()

        buyCalls.value = Unit
    }

    fun ClickToBargaining() {
        recordScenarioStep()

        sources.app.navigator.startScreen(nav.stack.chatScreen())
    }

    fun PressBack() {
        recordScenarioStep()

        parentNavigator.backToPrevious()
    }
}
