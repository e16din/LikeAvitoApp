package me.likeavitoapp.screens.main.createad.steps

import me.likeavitoapp.get
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


    fun ChangeIsPremiumUseCase(enabled: Boolean) {
        recordScenarioStep(enabled)

        state.isPremiumEnabled.next(enabled)
        get.sources().app.activeCreateAdRequest!!.isPremiumEnabled = enabled
        updatePrice()
    }

    fun ChangeIsAutoupdateUseCase(enabled: Boolean) {
        recordScenarioStep(enabled)

        state.isAutoupdateEnabled.next(enabled)
        get.sources().app.activeCreateAdRequest!!.isAutoupdateEnabled = enabled
        updatePrice()
    }

    private fun updatePrice() {
        var sum = 0
        if (state.isPremiumEnabled.value) {
            sum += 300
        }
        if(state.isAutoupdateEnabled.value){
            sum += 90
        }

        state.price.next(sum)
        get.sources().app.activeCreateAdRequest!!.price = sum
    }

}