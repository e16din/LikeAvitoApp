package me.likeavitoapp.screens.main.tabs.profile.edit

import android.util.Base64
import me.likeavitoapp.get
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.User
import me.likeavitoapp.model.Worker
import me.likeavitoapp.model.load
import me.likeavitoapp.recordScenarioStep

class EditProfileScreen(
    val navigator: ScreensNavigator
) : IScreen {

    class State {
        private val user = get.sources().app.user.value!!

        val name = UpdatableState(user.name)
        val phone = UpdatableState(user.contacts.phone)
        val telegram = UpdatableState(user.contacts.telegram)
        val whatsapp = UpdatableState(user.contacts.whatsapp)
        val email = UpdatableState(user.contacts.email)

        val avatarPickerEnabled = UpdatableState(false)
        val updateUser: Worker<User?> = Worker(null)
        var photo: ByteArray? = null
    }

    val state = State()


    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ClickToEditPhotoUseCase() {
        recordScenarioStep()

        state.avatarPickerEnabled.next(true)
    }

    fun ChangeUserPhotoUseCase(bytes: ByteArray?) {
        recordScenarioStep()

        state.photo = bytes
    }

    fun ChangeWhatsappUseCase(whatsapp: String) {
        recordScenarioStep()

        state.whatsapp.next(whatsapp)
    }

    fun ChangeTelegramUseCase(telegram: String) {
        recordScenarioStep()

        state.telegram.next(telegram)
    }

    fun ChangeEmailUseCase(email: String) {
        recordScenarioStep()

        state.email.next(email)
    }

    fun ChangePhoneUseCase(phone: String) {
        recordScenarioStep()

        state.phone.next(phone)
    }

    fun ChangeUserNameUseCase(name: String) {
        recordScenarioStep()

        state.name.next(name)
    }

    fun CloseScreenUseCase() {
        recordScenarioStep()

//        state.user.photoUrl.free(EditProfileScreen::class)
    }

    fun ClickToCloseUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ClickToDoneUseCase() {
        recordScenarioStep()

        state.updateUser.load(onDone = { newUser ->
            get.sources().app.user.next(newUser)
        }) {

            if (state.photo != null) {
                val photoBase64 = Base64.encodeToString(state.photo, Base64.DEFAULT)
                get.sources().backend.userService.postPhoto(photoBase64)
            }

            val userResult = get.sources().backend.userService.updateUser(
                userId = get.sources().app.user.value!!.id,
                name = state.name.value,
                phone = state.phone.value,
                telegram = state.telegram.value,
                whatsapp = state.whatsapp.value,
                email = state.email.value,
            )
            return@load Pair(userResult.getOrNull(), userResult.isSuccess)
        }
    }
}