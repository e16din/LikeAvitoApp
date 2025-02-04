package me.likeavitoapp.screens.main.tabs

import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.intl.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import me.likeavitoapp.inverse
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.log
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.addetails.AdDetailsScreen
import me.likeavitoapp.screens.main.order.create.CreateOrderScreen
import me.likeavitoapp.setUi

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
            ad.isFavorite.setUi(!ad.isFavorite.value)

            sources.backend.adsService.updateFavoriteState(ad)
        }
    }


    fun ClickToBuyUseCase(ad: Ad) {
        recordScenarioStep()

        scope.launchWithHandler {
            state.reserve.loading.setUi(true)
            val result = sources.backend.cartService.reserve(adId = ad.id)
            state.reserve.loading.setUi(false)

            val isReserved = result.getOrNull()
            if (isReserved == true) {
                state.reserve.data.setUi(isReserved)
                parentNavigator.startScreen(
                    CreateOrderScreen(
                        ad = ad,
                        parentNavigator = parentNavigator
                    )
                )

                ad.reservedTimeMs = System.currentTimeMillis()

                startReserveTimer(ad)

            } else {
                state.reserve.loadingFailed.setUi(true)
            }
        }
    }

    private suspend fun startReserveTimer(ad: Ad) {
        fun getTimeMs(): Long = 20 * 60 * 1000 - (System.currentTimeMillis() - ad.reservedTimeMs!!)

        var timeMs = getTimeMs()
        while (timeMs > 0) {
            timeMs = getTimeMs()
            ad.timerLabel.setUi(ad.reservedTimeMs?.let {
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
}