package me.likeavitoapp.screens.main.createad.steps

import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.OwnAd
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.recordScenarioStep


class DescriptionStepScreen(
    val ownAd: OwnAd,
    val navigator: ScreensNavigator
) : IScreen {

    inner class State {

        val title = UpdatableState(ownAd.title ?: "")
        val description = UpdatableState(ownAd.description ?: "")

        val photos = ownAd.photoBytes
        val imagePickerEnabled = UpdatableState(false)
        val scrollPhotosToEnd = UpdatableState(false)
        val isBargainingEnabled = UpdatableState(
            ownAd.isBargainingEnabled
        )

    }

    val state = State()

    fun ChangeDescriptionUseCase(description: String) {
        recordScenarioStep()

        state.description.next(description)
        ownAd.description = description
    }

    fun ChangeTitleUseCase(title: String) {
        recordScenarioStep()

        state.title.next(title)
        ownAd.title = title
    }

    fun ClickToAddPhotoUseCase() {
        recordScenarioStep()

//        state.imagePickerEnabled.next(true)

        // test
        state.photos.add(ByteArray(1))

        state.scrollPhotosToEnd.next(true)
    }

    fun ChangePhotosUseCase(bytes: ByteArray?) {
        recordScenarioStep(bytes)

        state.imagePickerEnabled.next(false)
        bytes?.let {
            state.photos.add(it)
            ownAd.photoBytes.add(it)
            state.scrollPhotosToEnd.next(true)
        }
    }

    fun ClickToRemovePhotoUseCase(bytes: ByteArray?) {
        recordScenarioStep(bytes)

        state.photos.remove(bytes)
        ownAd.photoBytes.remove(bytes)
    }

    fun ChangeIsBargainingUseCase(enabled: Boolean) {
        recordScenarioStep(enabled)

        state.isBargainingEnabled.next(enabled)
        ownAd.isBargainingEnabled = enabled
    }

}