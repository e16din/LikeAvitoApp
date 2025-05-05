package me.likeavitoapp.screens.main.createad.steps

import me.likeavitoapp.get
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.OwnAd
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.recordScenarioStep


class DeliveryStepScreen(
    val ownAd: OwnAd,
    val navigator: ScreensNavigator
) : IScreen {

    inner class State {
        val types = UpdatableState(get.sources().app.pickupPointTypes)
        val selectedTypes = ownAd.selectedPickupPointTypes

        val address = UpdatableState(ownAd.address ?: "")
    }

    val state = State()

    fun ChangeAddressUseCase(address: String) {
        recordScenarioStep(address)

        state.address.next(address)
        ownAd.address = address
    }

    fun ChangeEnabledPointsType(typeId: Int, checked: Boolean) {
        if (checked) {
            state.selectedTypes.add(typeId)
            ownAd.selectedPickupPointTypes.add(typeId)

        } else {
            state.selectedTypes.remove(typeId)
            ownAd.selectedPickupPointTypes.remove(typeId)
        }
    }

}