package me.likeavitoapp.screens.main.tabs.favorites

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.inverse
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.log
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.StateValue
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.tabs.BaseAdScreen
import me.likeavitoapp.screens.main.tabs.search.SearchScreen


class FavoritesScreen(
    override val parentNavigator: ScreensNavigator,
    override val scope: CoroutineScope = provideCoroutineScope(),
    override val sources: DataSources = provideDataSources()
) : BaseAdScreen(parentNavigator, scope, sources) {

    class State(
        val ads: Loadable<SnapshotStateList<Ad>> = Loadable(mutableStateListOf<Ad>()),
        val moveToAdsEnabled: StateValue<Boolean> = StateValue(false)
    ) : BaseAdState()

    override val state = State()
    lateinit var tabsNavigator: ScreensNavigator

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
                    state.ads.data.set(newFavorites.toMutableStateList())
                    state.moveToAdsEnabled.set(newFavorites.isEmpty())
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
                        state.ads.data.set(newFavorites)
                        state.moveToAdsEnabled.set(newFavorites.isEmpty())
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
                    state.ads.data.value.forEach { favorite ->
                        sources.app.ads.firstOrNull { it.id == favorite.id }?.apply {
                            log("clear: ${id}")
                            isFavorite.set(false)
                        }
                    }

                    state.ads.data.set(mutableStateListOf())
                    state.moveToAdsEnabled.set(true)
                }
            )
        }
    }

    fun ClickToMoveToAdsUseCase() {
        val searchScreen =
            tabsNavigator.getScreenOrNull(SearchScreen::class)!! // NOTE: please throw NPE if it is null here
        tabsNavigator.startScreen(searchScreen, clearStack = true)
    }
}