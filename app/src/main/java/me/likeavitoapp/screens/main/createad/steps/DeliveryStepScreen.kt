package me.likeavitoapp.screens.main.createad.steps

import me.likeavitoapp.get
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.recordScenarioStep


class DeliveryStepScreen(
    val navigator: ScreensNavigator
) : IScreen {

    class State {
        val types = UpdatableState(get.sources().app.pickupPointTypes)
        val selectedTypes = mutableListOf<Int>() // <PickupPointType.id>
        val address = UpdatableState("")
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

    }

    fun SelectEnabledPointsType(typeId: Int) {
        if(state.selectedTypes.contains(typeId)) {
            state.selectedTypes.remove(typeId)

        } else {
            state.selectedTypes.add(typeId)
        }

        // to update list
        state.types.next(get.sources().app.pickupPointTypes.toList())
    }

    fun ChangeAddressUseCase(address: String) {
        recordScenarioStep(address)

        state.address.next(address)
    }

    fun ClickToNextUseCase() {
        recordScenarioStep()

        get.sources().app.activeCreateAdRequest?.let {
            it.selectedTypes = state.selectedTypes
            it.address = state.address.value
        }

        navigator.startScreen(
            FinalStepScreen(navigator)
        )
    }

}