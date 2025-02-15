package me.likeavitoapp.screens.main.tabs.favorites

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.inverse
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.mainSet
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.Worker
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState

import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.addetails.AdDetailsScreen
import me.likeavitoapp.screens.main.tabs.BaseAdContainerScreen
import me.likeavitoapp.screens.main.tabs.search.SearchScreen


class FavoritesScreen(
    override val navigator: ScreensNavigator,
    override val scope: CoroutineScope = mainSet.provideCoroutineScope(),
    override val sources: DataSources = mainSet.provideDataSources(),
    override val state: State = State()
) : BaseAdContainerScreen(navigator, scope, sources, state) {

    class State(
        val favorites: Worker<SnapshotStateList<Ad>> = Worker(mutableStateListOf<Ad>()),
        val moveToAdsEnabled: UpdatableState<Boolean> = UpdatableState(false)
    ) : BaseAdContainerState()

    lateinit var tabsNavigator: ScreensNavigator

    private fun loadFavorites() {
        scope.launchWithHandler {
            state.favorites.load(
                loading = {
                    return@load sources.backend.adsService.getFavorites()
                },
                onSuccess = { newFavorites ->
                    state.favorites.output.post(newFavorites.toMutableStateList())
                    state.moveToAdsEnabled.post(newFavorites.isEmpty())
                }
            )
        }
    }

    override fun ClickToFavoriteUseCase(ad: Ad) {
        ad.isFavorite.inverse()

        scope.launchWithHandler {
            state.favorites.load(
                loading = {
                    return@load sources.backend.adsService.updateFavoriteState(ad)
                },
                onSuccess = { success ->
                    if (success) {
                        val newFavorites = state.favorites.output.value.apply {
                            remove(ad)
                        }
                        state.favorites.output.post(newFavorites)
                        state.moveToAdsEnabled.post(newFavorites.isEmpty())
                    }
                }
            )
        }
    }

    fun StartScreenUseCase() {
        recordScenarioStep()

        loadFavorites()
    }

    fun ClickToAdUseCase(ad: Ad) {
        recordScenarioStep()

        navigator.startScreen(
            AdDetailsScreen(
                ad = ad,
                scope = scope,
                navigator = navigator,
                sources = sources
            )
        )
    }

    fun ClickToClearAllUseCase() {
        recordScenarioStep()

        scope.launchWithHandler {
            state.favorites.load(
                loading = {
                    return@load sources.backend.adsService.deleteAllFavorites()
                },
                onSuccess = { data ->
//                    state.favorites.data.value.forEach { favorite ->
//                        sources.app.ads.firstOrNull { it.id == favorite.id }?.apply {
//                            isFavorite.post(false)
//                        }
//                    }

                    state.favorites.output.post(mutableStateListOf())
                    state.moveToAdsEnabled.post(true)
                }
            )
        }
    }

    fun ClickToMoveToAdsUseCase() {
        val searchScreen =
            tabsNavigator.getScreenOrNull(SearchScreen::class)!! // NOTE: please throw NPE if it is null here
        tabsNavigator.startScreen(searchScreen, clearAll = true)
    }
}