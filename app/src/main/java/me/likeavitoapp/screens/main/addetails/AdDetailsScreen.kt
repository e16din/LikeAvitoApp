package me.likeavitoapp.screens.main.addetails

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.inverse
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IMessage
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.tabs.BaseAdScreen
import me.likeavitoapp.screens.main.tabs.chat.ChatScreen


class AdDetailsScreen(
    ad: Ad,
    override val parentNavigator: ScreensNavigator,
    override val scope: CoroutineScope = provideCoroutineScope(),
    override val sources: DataSources = provideDataSources()
) : BaseAdScreen(parentNavigator, scope, sources) {

    class State(
        val ad: Ad,
        val messages: MutableState<List<IMessage>> = mutableStateOf(emptyList()),
    ) : BaseAdState()

    override val state: State = State(ad)


    fun ClickToFavoriteUseCase() {
        recordScenarioStep()

        state.ad.isFavorite.inverse()
    }

    fun ClickToBargaining() {
        recordScenarioStep()

        parentNavigator.startScreen(
            ChatScreen(
                ad = state.ad,
                parentNavigator = parentNavigator
            )
        )
    }

    fun PressBack() {
        recordScenarioStep()

        parentNavigator.backToPrevious()
    }
}
