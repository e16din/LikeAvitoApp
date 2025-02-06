package me.likeavitoapp.screens.main.tabs

import androidx.compose.ui.text.intl.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import me.likeavitoapp.inverse
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.log
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.BaseScreen
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.addetails.AdDetailsScreen
import me.likeavitoapp.screens.main.order.create.CreateOrderScreen

open class BaseAdScreen(
    open val parentNavigator: ScreensNavigator,
    open val scope: CoroutineScope,
    open val sources: DataSources,
    open val state: BaseAdState = BaseAdState()
) : BaseScreen() {

    open class BaseAdState(
        val reserve: Loadable<Boolean> = Loadable(false)
    )

    private val timersMap = mutableMapOf<Long, Job>()

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
        recordScenarioStep(ad)

        scope.launchWithHandler {
            log("ad.isFavorite: ${ad.isFavorite.value}")
            ad.isFavorite.inverse()
            log("ad.isFavorite after: ${ad.isFavorite.value}")

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
            state.reserve.load(loading = {
                sources.backend.cartService.reserve(adId = ad.id)
            }, onSuccess = { isReserved ->
                if (isReserved == true) {
                    state.reserve.data.post(isReserved)

                    ad.reservedTimeMs = System.currentTimeMillis()

                    timersMap[ad.id] = startReserveTimer(ad)

                    parentNavigator.startScreen(createOrderScreen)

                } else {
                    state.reserve.loadingFailed.post(true)
                }
            })
        }
    }

    private fun startReserveTimer(ad: Ad) = scope.launchWithHandler {
        fun getTimeMs(): Long = 20 * 60 * 1000 - (System.currentTimeMillis() - ad.reservedTimeMs!!)

        var timeMs = getTimeMs()
        while (timeMs > 0) {
            timeMs = getTimeMs()
            ad.timerLabel.post(ad.reservedTimeMs?.let {
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

        timersMap[ad.id]?.cancel()
        ad.timerLabel.post("")
        ad.reservedTimeMs = null
    }

    open fun CloseScreenUseCase() {
        recordScenarioStep()

        timersMap.values.forEach {
            it.cancel()
        }
    }
}