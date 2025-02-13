package me.likeavitoapp.screens.main.tabs.profile.edit

import android.util.Base64
import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.User
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep

class EditProfileScreen(
    val navigator: ScreensNavigator,
    val scope: CoroutineScope = provideCoroutineScope(),
    val sources: DataSources = provideDataSources(),
    user: User = sources.app.user.value!!
) : IScreen {

    class State(
        val user: User,
        val userPickerEnabled: UpdatableState<Boolean> = UpdatableState(false),
        val updateUser: Loadable<User> = Loadable(user),
        var photo: ByteArray? = null
    )

    val state = State(user.copy())


    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ClickToEditPhotoUseCase() {
        recordScenarioStep()

        scope.launchWithHandler {
            state.userPickerEnabled.post(true)
        }
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

        state.updateUser.loading.repostTo(
            sources.app.rootScreen.state.loadingEnabled
        )

        scope.launchWithHandler {
            state.updateUser.load(loading = {
                val photoBase64 = Base64.encodeToString(state.photo, Base64.DEFAULT)
                sources.backend.userService.postPhoto(photoBase64)

                return@load sources.backend.userService.updateUser(
                    name = state.user.name,
                    phone = state.user.contacts.phone,
                    telegram = state.user.contacts.telegram,
                    whatsapp = state.user.contacts.whatsapp,
                    email = state.user.contacts.email,
                )

            }, onSuccess = { newUser ->
                sources.app.user.post(newUser)
            })
        }
    }
}