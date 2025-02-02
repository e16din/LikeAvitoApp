package me.likeavitoapp.screens.main.tabs.favorites

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.inverse
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.tabs.AdsListScreen
import me.likeavitoapp.screens.main.tabs.search.SearchScreen


class FavoritesScreen(
    override val parentNavigator: ScreensNavigator,
    override val scope: CoroutineScope = provideCoroutineScope(),
    override val sources: DataSources = provideDataSources()
) : AdsListScreen(parentNavigator, scope, sources) {

    class State2(val moveToAdsEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false))

    val state2 = State2()
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
                    state.ads.data.value = newFavorites
                    state2.moveToAdsEnabled.value = newFavorites.isEmpty()
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
                        val newFavorites = state.ads.data.value.toMutableList().apply {
                            remove(ad)
                        }
                        state.ads.data.value = newFavorites
                        state2.moveToAdsEnabled.value = newFavorites.isEmpty()
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
                    state.ads.data.value = emptyList()
                    state2.moveToAdsEnabled.value = true
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