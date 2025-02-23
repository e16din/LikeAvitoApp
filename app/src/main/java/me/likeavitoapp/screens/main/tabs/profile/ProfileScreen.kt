package me.likeavitoapp.screens.main.tabs.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import me.likeavitoapp.UnauthorizedException
import me.likeavitoapp.get
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.User
import me.likeavitoapp.model.Worker
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.tabs.profile.edit.EditProfileScreen


class ProfileScreen(
    val navigator: ScreensNavigator,
    user: User = get.sources().app.user.value!!
) : IScreen {

    class State(
        val user: User,
        val logout: Worker<Unit> = Worker(Unit)
        )

    val state = State(user)

    fun ClickToContactUseCase(label:String, value: String) {
        recordScenarioStep()

        val clipboard = get.appContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, value)
        clipboard.setPrimaryClip(clip)
    }

    fun ClickToEditProfileUseCase() {
        recordScenarioStep()

        navigator.startScreen(
            EditProfileScreen(navigator)
        )
    }

    fun CloseScreenUseCase() {
        recordScenarioStep()

//        state.user.photoUrl.free(ProfileScreen::class)
    }

    fun ClickToLogoutUseCase() {
        recordScenarioStep()

        get.scope().launchWithHandler {
            state.logout.load(loading = {
                return@load get.sources().backend.userService.logout()
            }, onSuccess = { success ->
                throw UnauthorizedException()
            })
        }
    }

}