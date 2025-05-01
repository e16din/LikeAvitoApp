package me.likeavitoapp.screens.main.createad.steps

import androidx.compose.runtime.mutableStateListOf
import me.likeavitoapp.get
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.recordScenarioStep


class DescriptionStepScreen(
    val navigator: ScreensNavigator
) : IScreen {

    class State {
        val title = UpdatableState("")
        val description = UpdatableState("")

        val photos = mutableStateListOf<ByteArray>() // <file path>
        val imagePickerEnabled = UpdatableState(false)
        val scrollPhotosToEnd = UpdatableState(false)
        val isBargainingEnabled = UpdatableState(false)

    }

    val state = State()

    fun ChangeDescriptionUseCase(description: String) {
        recordScenarioStep()

        state.description.next(description)
        get.sources().app.activeCreateAdRequest!!.description = description
    }

    fun ChangeTitleUseCase(title: String) {
        recordScenarioStep()

        state.title.next(title)
        get.sources().app.activeCreateAdRequest!!.title = title
    }

    fun ClickToAddPhotoUseCase() {
        recordScenarioStep()

//        state.imagePickerEnabled.next(true)

        // test
        state.photos.add(ByteArray(1))
        get.sources().app.activeCreateAdRequest!!.photos.add(ByteArray(1))

        state.scrollPhotosToEnd.next(true)
    }

    fun ChangePhotosUseCase(bytes: ByteArray?) {
        recordScenarioStep(bytes)

        state.imagePickerEnabled.next(false)
        bytes?.let {
            state.photos.add(it)
            get.sources().app.activeCreateAdRequest!!.photos.add(it)
            state.scrollPhotosToEnd.next(true)
        }
    }

    fun ClickToRemovePhotoUseCase(bytes: ByteArray?) {
        recordScenarioStep(bytes)

        state.photos.remove(bytes)
        get.sources().app.activeCreateAdRequest!!.photos.remove(bytes)
    }

    fun ChangeIsBargainingUseCase(enabled: Boolean) {
        recordScenarioStep(enabled)

        state.isBargainingEnabled.next(enabled)
        get.sources().app.activeCreateAdRequest!!.isBargainingEnabled = enabled
    }

}