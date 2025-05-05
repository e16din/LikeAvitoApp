package me.likeavitoapp.screens.main.createad.steps

import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.OwnAd
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.recordScenarioStep


class FinalStepScreen(
    val ownAd: OwnAd,
    val navigator: ScreensNavigator
) : IScreen {

    inner class State {
        val isPremiumEnabled = UpdatableState(ownAd.isPremiumEnabled) // 300r
        val isAutoupdateEnabled = UpdatableState(ownAd.isAutoupdateEnabled) // 90r
        val price = UpdatableState(0)
    }

    val state = State()

    init {
        updatePrice()
    }

    fun ChangeIsPremiumUseCase(enabled: Boolean) {
        recordScenarioStep(enabled)

        state.isPremiumEnabled.next(enabled)
        ownAd.isPremiumEnabled = enabled
        updatePrice()
    }

    fun ChangeIsAutoupdateUseCase(enabled: Boolean) {
        recordScenarioStep(enabled)

        state.isAutoupdateEnabled.next(enabled)
        ownAd.isAutoupdateEnabled = enabled
        updatePrice()
    }

    private fun updatePrice() {
        var sum = 0
        if (state.isPremiumEnabled.value) {
            sum += 300
        }
        if (state.isAutoupdateEnabled.value) {
            sum += 90
        }

        state.price.next(sum)
        ownAd.price = sum
    }

}