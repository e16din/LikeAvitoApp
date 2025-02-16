package me.likeavitoapp.screens.main.tabs

import androidx.compose.ui.text.intl.Locale
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import me.likeavitoapp.inverse
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.get
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.Worker
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.order.create.CreateOrderScreen
import me.likeavitoapp.screens.main.tabs.chat.ChatScreen

open class BaseAdContainerScreen(
    open val navigator: ScreensNavigator,
    open val state: BaseAdContainerState
) : IScreen {

    open class BaseAdContainerState(
        val reserve: Worker<Boolean> = Worker(false)
    )

    val timersMap = mutableMapOf<Long, Job>()

    open fun ClickToFavoriteUseCase(ad: Ad) {
        recordScenarioStep(ad)

        get.scope().launchWithHandler {
            ad.isFavorite.inverse()

            get.sources().backend.adsService.updateFavoriteState(ad)
        }
    }


    fun ClickToBuyUseCase(ad: Ad) {
        recordScenarioStep()

        val createOrderScreen = CreateOrderScreen(ad, navigator)
        if (ad.reservedTimeMs != null) {
            navigator.startScreen(createOrderScreen)
            return
        }

        get.scope().launchWithHandler {
            state.reserve.load(loading = {
                get.sources().backend.orderService.reserve(adId = ad.id)
            }, onSuccess = { isReserved ->
                if (isReserved == true) {
                    state.reserve.output.post(true)

                    ad.reservedTimeMs = System.currentTimeMillis()

                    timersMap[ad.id] = startReserveTimer(ad)

                    navigator.startScreen(createOrderScreen)

                } else {
                    state.reserve.fail.post(true)
                }
            })
        }
    }

    fun startReserveTimer(ad: Ad) = get.scope().launchWithHandler {
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

        navigator.startScreen(
            ChatScreen(ad, navigator)
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
    }
}