package me.likeavitoapp.screens.main.addetails

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.inverse
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IMessage
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.order.create.CreateOrderScreen
import me.likeavitoapp.screens.main.tabs.chat.ChatScreen


class AdDetailsScreen(
    ad: Ad,
    val scope: CoroutineScope,
    val parentNavigator: ScreensNavigator,
    val sources: DataSources,
) : IScreen {

    val state: State = State(ad)

    class State(
        val ad: Ad,
        val messages: MutableStateFlow<List<IMessage>> = MutableStateFlow(emptyList()),
        val reserve: Loadable<Boolean> = Loadable(false)
    )

    fun ClickToFavoriteUseCase() {
        recordScenarioStep()

        state.ad.isFavorite.inverse()
    }

    fun ClickToBuyUseCase() {
        recordScenarioStep()

        scope.launchWithHandler {
            state.reserve.loading.value = true
            val result = sources.backend.cartService.reserve(adId = state.ad.id)
            state.reserve.loading.value = false

            val isReserved = result.getOrNull()
            if (isReserved == true) {
                state.reserve.data.value = isReserved
                parentNavigator.startScreen(
                    CreateOrderScreen(
                        ad = state.ad,
                        parentNavigator = parentNavigator
                    )
                )
            } else {
                state.reserve.loadingFailed.value = true
            }
        }
    }

    fun ClickToBargaining() {
        recordScenarioStep()

        parentNavigator.startScreen(
            ChatScreen(
                ad = state.ad,
                parentNavigator = parentNavigator
            )
        )
    }

    fun PressBack() {
        recordScenarioStep()

        parentNavigator.backToPrevious()
    }
}
