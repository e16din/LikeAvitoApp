package me.likeavitoapp.screens.main.tabs.favorites

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.inverse
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.tabs.BaseAdScreen
import me.likeavitoapp.screens.main.tabs.search.SearchScreen
import me.likeavitoapp.setUi


class FavoritesScreen(
    override val parentNavigator: ScreensNavigator,
    override val scope: CoroutineScope = provideCoroutineScope(),
    override val sources: DataSources = provideDataSources()
) : BaseAdScreen(parentNavigator, scope, sources) {

    class State(
        val ads: Loadable<MutableList<Ad>> = Loadable(mutableListOf<Ad>()),
        val moveToAdsEnabled: MutableState<Boolean> = mutableStateOf(false)
    ) : BaseAdState()

    override val state = State()
    lateinit var navigator: ScreensNavigator

    fun StartScreenUseCase() {
        recordScenarioStep()

        loadFavorites()
    }

    private fun loadFavorites() {
        scope.launchWithHandler {
            state.ads.load(
                loading = {
                    return@load sources.backend.adsService.getFavorites()
                },
                onSuccess = { newFavorites ->
                    state.ads.data.setUi(newFavorites.toMutableList())
                    state.moveToAdsEnabled.setUi(newFavorites.isEmpty())
                }
            )
        }
    }

    override fun ClickToFavoriteUseCase(ad: Ad) {
        scope.launchWithHandler {
            ad.isFavorite.inverse()

            state.ads.load(
                loading = {
                    return@load sources.backend.adsService.updateFavoriteState(ad)
                },
                onSuccess = { success ->
                    if (success) {
                        val newFavorites = state.ads.data.value.apply {
                            remove(ad)
                        }
                        state.ads.data.setUi(newFavorites)
                        state.moveToAdsEnabled.setUi(newFavorites.isEmpty())
                    }

                }
            )
        }
    }

    fun ClickToClearAllUseCase() {
        recordScenarioStep()

        scope.launchWithHandler {
            state.ads.load(
                loading = {
                    return@load sources.backend.adsService.deleteAllFavorites()
                },
                onSuccess = { data ->
                    state.ads.data.setUi(mutableListOf())
                    state.moveToAdsEnabled.setUi(true)
                }
            )
        }
    }

    fun ClickToMoveToAdsUseCase() {
        val searchScreen =
            navigator.getScreenOrNull(SearchScreen::class)!! // NOTE: please throw NPE if it is null here
        navigator.startScreen(searchScreen, clearStack = true)
    }
}