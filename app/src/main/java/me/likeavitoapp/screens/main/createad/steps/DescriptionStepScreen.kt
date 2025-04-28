package me.likeavitoapp.screens.main.createad.steps

import androidx.compose.runtime.mutableStateListOf
import me.likeavitoapp.R
import me.likeavitoapp.get
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.recordScenarioStep


//class CategoryStepScreen(
//class DeliveryStepScreen(
//class FinalStepScreen( isPremium, "Объявление будет активно 21 день, обновлять автоматически?"
// "Цена: Без оплаты (или 300 за премиум + 90 за обновление)"

// Доставку убрать совсем
// < Вернуться на шаг описания, < Вернуться на шаг категории, < Вернуться на шаг доставки
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

    fun StartScreenUseCase() {
        recordScenarioStep()

    }

    fun PressBackUseCase() {
        recordScenarioStep()

        get.sources().app.mainScreen.returnToActiveTab()
    }

    fun ClickToDoneUseCase() {
        recordScenarioStep()

        nextStep()
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

    fun ClickToNextUseCase() {
        recordScenarioStep()

        nextStep()
    }

    private fun checkIsValid(): Boolean {
        val fieldName = if (state.title.value.isEmpty()) {
            get.sources().platform.getString(R.string.title_arg)
        } else if (state.description.value.isEmpty()) {
            get.sources().platform.getString(R.string.description_arg)
        } else if (state.photos.isEmpty()) {
            get.sources().platform.getString(R.string.photo_arg)
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

    private fun nextStep() {
        if (checkIsValid()) {
            get.sources().app.activeCreateAdRequest?.let {
                it.title = state.title.value
                it.description = state.description.value
                it.photos = state.photos
                it.isBargainingEnabled = state.isBargainingEnabled.value
            }
            get.sources().app.loading.next(false)
            navigator.startScreen(
                CategoryStepScreen(navigator)
            )
        }
    }

}