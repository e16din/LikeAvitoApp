package me.likeavitoapp.screens.main.createad.steps

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.likeavitoapp.R
import me.likeavitoapp.developer.primitives.work
import me.likeavitoapp.get
import me.likeavitoapp.inverse
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.recordScenarioStep


class FinalStepScreen(
    val navigator: ScreensNavigator
) : IScreen {

    class State {
        val isPremiumEnabled = UpdatableState(false) // 300r
        val isAutoupdateEnabled = UpdatableState(false) // 90r
        val price = UpdatableState(0)
    }

    val state = State()

    fun StartScreenUseCase() {
        recordScenarioStep()

    }

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ClickToDoneUseCase() {
        recordScenarioStep()

        if (state.price.value == 0) {
            createAd()

        } else {
            get.sources().app.pay { success ->
                createAd()
            }
        }
    }

    fun ClickToIsPremiumUseCase() {
        recordScenarioStep()

        state.isPremiumEnabled.inverse()
        updatePrice()
    }

    fun ChangeIsPremiumUseCase(enabled: Boolean) {
        recordScenarioStep(enabled)

        state.isPremiumEnabled.next(enabled)
        updatePrice()
    }

    fun ClickToIsAutoupdateUseCase() {
        recordScenarioStep()

        state.isAutoupdateEnabled.inverse()
        updatePrice()
    }

    fun ChangeIsAutoupdateUseCase(enabled: Boolean) {
        recordScenarioStep(enabled)

        state.isAutoupdateEnabled.next(enabled)
        updatePrice()
    }

    private fun updatePrice() {
        var sum = 0
        if (state.isPremiumEnabled.value) {
            sum += 300
        } else {
            sum += 90
        }

        state.price.next(sum)
    }

    fun ClickToCreateAdUseCase() {
        recordScenarioStep()

        ClickToDoneUseCase()
    }

    private fun checkIsValid(): Boolean {
        return false
    }

    private fun createAd() {
        if (checkIsValid()) {
            get.sources().app.loading.next(true)
            work {
                val result = get.sources().backend.adsService.createAd(
                    get.sources().app.activeCreateAdRequest!!
                )
                withContext(Dispatchers.Main) {
                    get.sources().app.loading.next(false)
                    if (result.getOrNull() == true) {

                        get.sources().app.message.next(
                            get.sources().platform.getString(R.string.create_ad_success_message)
                        )
                    }
                }
            }
        }
    }

}