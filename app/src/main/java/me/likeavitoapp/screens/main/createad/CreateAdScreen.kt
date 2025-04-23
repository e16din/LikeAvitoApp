package me.likeavitoapp.screens.main.createad

import androidx.compose.runtime.mutableStateListOf
import me.likeavitoapp.get
import me.likeavitoapp.inverse
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.Category
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.recordScenarioStep


class CreateAdScreen(
    val navigator: ScreensNavigator
) : IScreen {

    class State {
        val title = UpdatableState("")
        val description = UpdatableState("")
        val address = UpdatableState("")
        val price = UpdatableState(0)
        val category = UpdatableState<Category?>(null)

        val photos = mutableStateListOf<ByteArray?>() // <file path>
        val imagePickerEnabled = UpdatableState(false)
        val scrollPhotosToEnd = UpdatableState(false)
        val isBargainingEnabled = UpdatableState(false)
        val isDeliveryEnabled = UpdatableState(false)

        var exitDialogEnabled = UpdatableState(false)
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

        createAd()
    }

    fun ChangeDescriptionUseCase(description: String) {
        recordScenarioStep()

        state.description.next(description)
    }

    fun ChangeTitleUseCase(title: String) {
        recordScenarioStep()

        state.title.next(title)
    }

    fun ClickToAddPhotoUseCase() {
        recordScenarioStep()

        state.imagePickerEnabled.next(true)

        // test
//        state.photos.add(null)
//        state.scrollToEnd.next(true)
    }

    fun ChangePhotosUseCase(bytes: ByteArray?) {
        recordScenarioStep(bytes)

        state.imagePickerEnabled.next(false)
        bytes?.let {
            state.photos.add(it)
            state.scrollPhotosToEnd.next(true)
        }
    }

    fun ClickToRemovePhotoUseCase(bytes: ByteArray?) {
        recordScenarioStep(bytes)

        state.photos.remove(bytes)
    }

    fun ChangeIsBargainingUseCase(enabled: Boolean) {
        recordScenarioStep(enabled)

        state.isBargainingEnabled.next(enabled)
    }

    fun ClickToIsBargainingUseCase() {
        recordScenarioStep()

        state.isBargainingEnabled.inverse()
    }

    fun ClickToIsDeliveryUseCase() {
        recordScenarioStep()

        state.isDeliveryEnabled.inverse()
    }

    fun ChangeIsDeliveryUseCase(enabled: Boolean) {
        recordScenarioStep(enabled)

        state.isDeliveryEnabled.next(enabled)
    }

    fun ChangePriceUseCase(price: String) {
        recordScenarioStep(price)

        state.price.next(price.toInt())
    }

    fun ChangeAddressUseCase(address: String) {
        recordScenarioStep(address)

        state.isDeliveryEnabled.next(address)
    }

    fun ClickToCreateAdUseCase() {
        recordScenarioStep()

        createAd()
    }

    private fun checkIsValid(): Boolean {
        return false
    }
    
    private fun createAd() {
        if (checkIsValid()) {
            val newAd = Ad(
                id = TODO(),
                title = TODO(),
                description = TODO(),
                photoUrls = TODO(),
                contacts = TODO(),
                price = TODO(),
                isBargainingEnabled = TODO(),
                isPremium = TODO(),
                categoryId = TODO(),
                regionId = TODO(),
                address = TODO(),
                isPickupEnabled = TODO(),
                isDeliveryEnabled = TODO(),
                enabledPickupPointTypes = TODO(),
                owner = TODO(),
                reservedTimeMs = TODO()
            )
            get.sources().backend.adsService.createAd(newAd)
        }
    }
}