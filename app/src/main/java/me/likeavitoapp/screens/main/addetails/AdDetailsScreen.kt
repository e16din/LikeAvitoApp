package me.likeavitoapp.screens.main.addetails

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.inverse
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IMessage
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.StateValue
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
        val messages: StateValue<List<IMessage>> = StateValue(emptyList()),
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
