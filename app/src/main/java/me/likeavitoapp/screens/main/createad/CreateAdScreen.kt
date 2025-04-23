package me.likeavitoapp.screens.main.createad

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.Category
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.Worker
import me.likeavitoapp.recordScenarioStep


class CreateAdScreen(
    val navigator: ScreensNavigator
) : IScreen {

    class State {
        val title = UpdatableState("")
        val photos = mutableStateListOf<ByteArray?>() // <file path>
        val imagePickerEnabled = UpdatableState(false)
        val scrollToEnd = UpdatableState(false)

        var adCreated by mutableStateOf(Worker(false))
        var exitDialog by mutableStateOf(false)
    }

    val state: State = State()

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
            state.scrollToEnd.next(true)
        }
    }

    fun ClickToRemovePhotoUseCase(bytes: ByteArray?) {
        recordScenarioStep(bytes)

        state.photos.remove(bytes)
    }

    class Input {
        var onTitleChanged: (title: String) -> Unit = {}
        var onDescriptionChanged: (title: String) -> Unit = {}
        var onPriceChanged: (price: Int) -> Unit = {}
        var onAddPhotoClick: () -> Unit = {}
        var onContactsChanged: (price: Int) -> Unit = {}
        var onPremiumStatusChanged: (isPremium: Boolean) -> Unit = {}
        var onAddressChanged: (address: Ad.Address) -> Unit = {}
        var onCategoryChanged: (category: Category) -> Unit = {}
        var onDeliveryEnableChanged: (enable: Boolean) -> Unit = {}
        var onPickupEnableChanged: (enable: Boolean) -> Unit = {}

        var onBackClick: () -> Unit = {}
        var onDoneClick: () -> Unit = {}
    }

    // UseCases:

}