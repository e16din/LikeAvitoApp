package me.likeavitoapp.screens.main.tabs.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.User
import me.likeavitoapp.provideAndroidAppContext
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.tabs.profile.edit.EditProfileScreen


class ProfileScreen(
    val scope: CoroutineScope = provideCoroutineScope(),
    val parentNavigator: ScreensNavigator,
    val sources: DataSources = provideDataSources(),
    user: User = sources.app.user!!
) : IScreen {

    class State(val user: User)

    val state = State(user)

    lateinit var tabsNavigator: ScreensNavigator

    fun ClickToContactUseCase(label:String, value: String) {
        recordScenarioStep()

        val clipboard = provideAndroidAppContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, value);
        clipboard.setPrimaryClip(clip);
    }

    fun ClickToEditProfileUseCase() {
        recordScenarioStep()

        parentNavigator.startScreen(
            EditProfileScreen(parentNavigator)
        )
    }

}