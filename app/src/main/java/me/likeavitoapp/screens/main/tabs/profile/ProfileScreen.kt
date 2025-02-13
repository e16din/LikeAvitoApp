package me.likeavitoapp.screens.main.tabs.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.UnauthorizedException
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.User
import me.likeavitoapp.provideAndroidAppContext
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.tabs.profile.edit.EditProfileScreen


class ProfileScreen(
    val scope: CoroutineScope = provideCoroutineScope(),
    val navigator: ScreensNavigator,
    val sources: DataSources = provideDataSources(),
    user: User = sources.app.user.value!!
) : IScreen {

    class State(
        val user: User,
        val logout: Loadable<Unit> = Loadable(Unit)
        )

    val state = State(user)

    fun ClickToContactUseCase(label:String, value: String) {
        recordScenarioStep()

        val clipboard = provideAndroidAppContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, value);
        clipboard.setPrimaryClip(clip);
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

        scope.launchWithHandler {
            state.logout.load(loading = {
                return@load sources.backend.userService.logout()
            }, onSuccess = { success ->
                throw UnauthorizedException()
            })
        }
    }

}