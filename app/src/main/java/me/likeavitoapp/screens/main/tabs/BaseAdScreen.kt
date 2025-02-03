package me.likeavitoapp.screens.main.tabs

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.inverse
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.addetails.AdDetailsScreen
import me.likeavitoapp.screens.main.order.create.CreateOrderScreen

open class BaseAdScreen(
    open val parentNavigator: ScreensNavigator,
    open val scope: CoroutineScope,
    open val sources: DataSources,
) : IScreen {

    open class BaseAdState(
        val reserve: Loadable<Boolean> = Loadable(false)
    )

    open val state = BaseAdState()

    fun ClickToAdUseCase(ad: Ad) {
        recordScenarioStep()

        parentNavigator.startScreen(
            AdDetailsScreen(
                ad = ad,
                scope = scope,
                parentNavigator = parentNavigator,
                sources = sources
            )
        )
    }

    open fun ClickToFavoriteUseCase(ad: Ad) {
        recordScenarioStep()

        scope.launchWithHandler {
            ad.isFavorite.inverse()
            sources.backend.adsService.updateFavoriteState(ad)
        }
    }

    fun ClickToBuyUseCase(ad: Ad) {
        recordScenarioStep()

        scope.launchWithHandler {
            state.reserve.loading.value = true
            val result = sources.backend.cartService.reserve(adId = ad.id)
            state.reserve.loading.value = false

            val isReserved = result.getOrNull()
            if (isReserved == true) {
                state.reserve.data.value = isReserved
                parentNavigator.startScreen(
                    CreateOrderScreen(
                        ad = ad,
                        parentNavigator = parentNavigator
                    )
                )
            } else {
                state.reserve.loadingFailed.value = true
            }
        }
    }

    fun ClickToBargainingUseCase(ad: Ad) {
        recordScenarioStep()
    }
}