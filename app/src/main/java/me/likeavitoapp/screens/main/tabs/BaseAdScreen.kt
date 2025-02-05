package me.likeavitoapp.screens.main.tabs

import androidx.compose.ui.text.intl.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import me.likeavitoapp.defaultContext
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

    private val timersScope = CoroutineScope(defaultContext)

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
            ad.isFavorite.set(!ad.isFavorite.value)

            sources.backend.adsService.updateFavoriteState(ad)
        }
    }


    fun ClickToBuyUseCase(ad: Ad) {
        recordScenarioStep()

        val createOrderScreen = CreateOrderScreen(
            ad = ad,
            parentNavigator = parentNavigator
        )
        if (ad.reservedTimeMs != null) {
            parentNavigator.startScreen(createOrderScreen)
            return
        }

        scope.launchWithHandler {
            state.reserve.loading.set(true)
            val result = sources.backend.cartService.reserve(adId = ad.id)
            state.reserve.loading.set(false)

            val isReserved = result.getOrNull()
            if (isReserved == true) {
                state.reserve.data.set(isReserved)

                ad.reservedTimeMs = System.currentTimeMillis()

                timersScope.launchWithHandler {
                    startReserveTimer(ad)
                }

                parentNavigator.startScreen(createOrderScreen)

            } else {
                state.reserve.loadingFailed.set(true)
            }
        }
    }

    private suspend fun startReserveTimer(ad: Ad) {
        fun getTimeMs(): Long = 20 * 60 * 1000 - (System.currentTimeMillis() - ad.reservedTimeMs!!)

        var timeMs = getTimeMs()
        while (timeMs > 0) {
            timeMs = getTimeMs()
            ad.timerLabel.set(ad.reservedTimeMs?.let {
                String.format(
                    Locale.current.platformLocale,
                    "%02d:%02d",
                    timeMs / 1000 / 60 % 60, timeMs / 1000 % 60
                )
            } ?: "")
            delay(1000)
        }
        ad.reservedTimeMs = null
    }

    fun ClickToBargainingUseCase(ad: Ad) {
        recordScenarioStep()
    }

    fun ClickToCloseTimerLabel(ad: Ad) {
        recordScenarioStep()


    }

    open fun CloseScreenUseCase() {
        recordScenarioStep()

        timersScope.cancel()
    }
}