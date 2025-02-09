package me.likeavitoapp.screens.main.tabs

import androidx.compose.ui.text.intl.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import me.likeavitoapp.inverse
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.log
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.order.create.CreateOrderScreen
import me.likeavitoapp.screens.main.tabs.chat.ChatScreen

open class BaseAdContainerScreen(
    open val parentNavigator: ScreensNavigator,
    open val scope: CoroutineScope,
    open val sources: DataSources,
    open val state: BaseAdContainerState
) : IScreen {

    open class BaseAdContainerState(
        val reserve: Loadable<Boolean> = Loadable(false)
    )

    val timersMap = mutableMapOf<Long, Job>()

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

        val createOrderScreen = CreateOrderScreen(ad, parentNavigator)
        if (ad.reservedTimeMs != null) {
            parentNavigator.startScreen(createOrderScreen)
            return
        }

        scope.launchWithHandler {
            state.reserve.load(loading = {
                sources.backend.cartService.reserve(adId = ad.id)
            }, onSuccess = { isReserved ->
                if (isReserved == true) {
                    state.reserve.data.post(true)

                    ad.reservedTimeMs = System.currentTimeMillis()

                    timersMap[ad.id] = startReserveTimer(ad)

                    parentNavigator.startScreen(createOrderScreen)

                } else {
                    state.reserve.loadingFailed.post(true)
                }
            })
        }
    }

    fun startReserveTimer(ad: Ad) = scope.launchWithHandler {
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
        recordScenarioStep(ad)

        parentNavigator.startScreen(
            ChatScreen(ad, parentNavigator)
        )
    }

    fun ClickToCloseTimerLabel(ad: Ad) {
        recordScenarioStep(ad)

        timersMap[ad.id]?.cancel()
        ad.timerLabel.post("")
        ad.reservedTimeMs = null
    }

    open fun CloseScreenUseCase() {
        recordScenarioStep()

//        timersMap.values.forEach {
//            it.cancel()
//        }
    }
}