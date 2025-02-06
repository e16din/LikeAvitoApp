package me.likeavitoapp.screens.main.tabs.profile.edit

import android.util.Base64
import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.BaseScreen
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.User
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep

class EditProfileScreen(
    val parentNavigator: ScreensNavigator,
    val scope: CoroutineScope = provideCoroutineScope(),
    val sources: DataSources = provideDataSources(),
    user: User = sources.app.user!!
) : BaseScreen() {

    class State(
        val user: User,
        val userPickerEnabled: UpdatableState<Boolean> = UpdatableState(false),
        val newPhoto: Loadable<ByteArray?> = Loadable(null)
    )

    val state = State(user)

    fun PressBack() {
        recordScenarioStep()

        parentNavigator.backToPrevious()
    }

    fun ClickToEditPhotoUseCase() {
        recordScenarioStep()

        scope.launchWithHandler {
            state.userPickerEnabled.post(true)
        }
    }

    fun ChangeUserPhotoUseCase(bytes: ByteArray?) {
        recordScenarioStep()

        val photoBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
        scope.launchWithHandler {
            state.newPhoto.load(loading = {
                return@load sources.backend.userService.postPhoto(photoBase64)

            }, onSuccess = { newPhoto -> //todo: replace with url in real app
                val bytes = Base64.decode(newPhoto, Base64.DEFAULT)
                if (bytes != null) {
                    state.newPhoto.data.post(bytes)
                } else {
                    state.newPhoto.loadingFailed.post(true)
                }
            })
        }

    }

    fun CloseScreenUseCase() {
        recordScenarioStep()

//        state.user.photoUrl.free(EditProfileScreen::class)
    }
}