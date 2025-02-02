package me.likeavitoapp.screens.main.tabs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.inverse
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.addetails.AdDetailsScreen

open class AdsListScreen(
    open val parentNavigator: ScreensNavigator,
    open val scope: CoroutineScope,
    open val sources: DataSources,
    ): IScreen {
        class State(
            val ads: Loadable<List<Ad>> = Loadable(emptyList<Ad>()),
            var adsPage: MutableStateFlow<Int> = MutableStateFlow(0)
        )

        val state = State()

        fun ClickToAdUseCase(ad: Ad) {
            recordScenarioStep()

            parentNavigator.startScreen(
                AdDetailsScreen(
                    ad = ad,
                    scope = scope,
                    parentNavigator = parentNavigator,
                    sources = sources
                )
            )
        }

        open fun ClickToFavoriteUseCase(ad: Ad) {
            recordScenarioStep()

            scope.launchWithHandler {
                ad.isFavorite.inverse()
                sources.backend.adsService.updateFavoriteState(ad)
            }
        }

        open fun ScrollToEndUseCase() {
            recordScenarioStep()
        }

        fun ClickToBuyUseCase() {
            recordScenarioStep()
        }

        fun ClickToBargainingUseCase() {
            recordScenarioStep()
        }
    }