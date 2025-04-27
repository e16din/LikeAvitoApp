package me.likeavitoapp.screens.main.createad.steps

import androidx.compose.runtime.mutableStateListOf
import me.likeavitoapp.R
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

    fun StartScreenUseCase() {
        recordScenarioStep()

    }

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ClickToDoneUseCase() {
        recordScenarioStep()

        ClickToNextUseCase()
    }

    fun ChangeAddressUseCase(address: String) {
        recordScenarioStep(address)

        state.address.next(address)
    }


    private fun checkIsValid(): Boolean {
        val ownerAddressId = 0
        val addressEnabled = state.selectedTypes.contains(ownerAddressId)

        val fieldName = if (state.selectedTypes.isEmpty()) {
            get.sources().platform.getString(R.string.possible_pickup_points_arg)
        } else if (addressEnabled && state.address.value.isEmpty()) {
            get.sources().platform.getString(R.string.address_arg)
        } else {
            null
        }

        fieldName?.let {
            get.sources().app.message.next(
                get.sources().platform.getString(R.string.fill_the_field_message, fieldName)
            )
            return false
        }

        return true
    }
    fun ClickToNextUseCase() {
        recordScenarioStep()

        if(checkIsValid()){
            val ownerAddressId = 0
            if(state.selectedTypes.contains(ownerAddressId)) {
                get.sources().app.activeCreateAdRequest?.let {
                    it.selectedTypes = state.selectedTypes
                    it.address = state.address.value
                }
            }

            navigator.startScreen(
                FinalStepScreen(navigator)
            )
        }
    }

    fun ChangeEnabledPointsType(typeId: Int, checked: Boolean) {
        if (checked) {
            state.selectedTypes.add(typeId)

        } else {
            state.selectedTypes.remove(typeId)
        }
    }

}