package me.likeavitoapp.screens.main.tabs.profile.edit

import android.util.Base64
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.get
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Worker
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.User
import me.likeavitoapp.model.act
import me.likeavitoapp.recordScenarioStep

class EditProfileScreen(
    val navigator: ScreensNavigator,
    user: User = get.sources().app.user.value!!
) : IScreen {

    class State(
        val user: User,
        val userPickerEnabled: UpdatableState<Boolean> = UpdatableState(false),
        val updateUser: Worker<User> = Worker(user),
        var photo: ByteArray? = null
    )

    val state = State(user.copy())


    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ClickToEditPhotoUseCase() {
        recordScenarioStep()

        state.userPickerEnabled.next(true)
    }

    fun ChangeUserPhotoUseCase(bytes: ByteArray?) {
        recordScenarioStep()

        state.photo = bytes
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

        state.updateUser.act(onDone = { newUser ->
            get.sources().app.user.next(newUser)
        }) {
            val photoBase64 = Base64.encodeToString(state.photo, Base64.DEFAULT)
            get.sources().backend.userService.postPhoto(photoBase64)

            val userResult = get.sources().backend.userService.updateUser(
                userId = state.user.id,
                name = state.user.name,
                phone = state.user.contacts.phone,
                telegram = state.user.contacts.telegram,
                whatsapp = state.user.contacts.whatsapp,
                email = state.user.contacts.email,
            )
            return@act Pair(userResult.getOrNull(), userResult.isSuccess)
        }
    }
}