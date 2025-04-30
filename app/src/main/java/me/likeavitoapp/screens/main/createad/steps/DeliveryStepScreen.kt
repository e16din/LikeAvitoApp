package me.likeavitoapp.screens.main.createad.steps

import androidx.compose.runtime.mutableStateListOf
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
        val selectedTypes = mutableStateListOf<Int>()  // <PickupPointType.id>
            .apply {
                addAll(types.value.map { it.id })
            }

        val address = UpdatableState("")
    }

    val state = State()

    fun ChangeAddressUseCase(address: String) {
        recordScenarioStep(address)

        state.address.next(address)
        get.sources().app.activeCreateAdRequest!!.address = address
    }

    fun ChangeEnabledPointsType(typeId: Int, checked: Boolean) {
        if (checked) {
            state.selectedTypes.add(typeId)
            get.sources().app.activeCreateAdRequest!!.selectedPickupPointTypes.add(typeId)

        } else {
            state.selectedTypes.remove(typeId)
            get.sources().app.activeCreateAdRequest!!.selectedPickupPointTypes.remove(typeId)
        }
    }

}