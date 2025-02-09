package me.likeavitoapp.screens.main.tabs.search

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.Debouncer
import me.likeavitoapp.bindScenarioDataSource
import me.likeavitoapp.inverse
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.log
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.Category
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.PriceRange
import me.likeavitoapp.model.Region
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.addetails.AdDetailsScreen
import me.likeavitoapp.screens.main.tabs.BaseAdContainerScreen


class SearchScreen(
    override val parentNavigator: ScreensNavigator,
    override val scope: CoroutineScope = provideCoroutineScope(),
    override val sources: DataSources = provideDataSources(),
    override val state: State = State()
) : BaseAdContainerScreen(parentNavigator, scope, sources, state) {

    class State(
        val ads: Loadable<SnapshotStateList<Ad>> = Loadable(mutableStateListOf<Ad>()),
        var adsPage: UpdatableState<Int> = UpdatableState(0)
    ) : BaseAdContainerState()

    val searchBar = SearchBar()
    val searchSettingsPanel = SearchSettingsPanel()

    suspend fun loadAds() {
        log("loadAds")
        state.ads.loading.repostTo(sources.app.rootScreen.state.loadingEnabled)
        state.ads.load(
            loading = {
                return@load sources.backend.adsService.getAds(
                    page = state.adsPage.value,
                    query = searchBar.state.query.value,
                    categoryId = searchSettingsPanel.state.selectedCategory.value?.id ?: 0,
                    range = searchSettingsPanel.state.priceRange.value,
                    regionId = searchSettingsPanel.state.selectedRegion.value?.id ?: 0
                )
            },
            onSuccess = { newAds ->
                with(newAds.toMutableStateList()) {
                    state.ads.data.post(this)
                    bindScenarioDataSource(Ad::class, this)
                }
            }
        )
    }

    suspend fun loadCategories() = with(searchSettingsPanel.state) {
        categories.load(
            loading = {
                return@load sources.backend.adsService.getCategories()
            },
            onSuccess = { newCategories ->
                categories.data.post(newCategories)
            }
        )
    }

    fun StartScreenUseCase() {
        recordScenarioStep()

        val ads = state.ads.data.value
        val isInited = ads.isEmpty()
        if (isInited) {
            scope.launchWithHandler {
                loadCategories()
                loadAds()
            }
        } else {
            ads.forEach {
                if (it.reservedTimeMs != null) {
                    timersMap[it.id] = startReserveTimer(it)
                }
            }

        }
    }

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

    fun ScrollToEndUseCase() {
        recordScenarioStep()

        if (!state.ads.data.value.isEmpty()) {
            scope.launchWithHandler {
                state.adsPage.post(state.adsPage.value + 1)
                loadAds()
            }
        }
    }

    fun CloseSearchSettingsPanelUseCase() {
        recordScenarioStep()

        scope.launchWithHandler {
            searchSettingsPanel.state.enabled.post(false)
            loadAds()
        }
    }

    inner class SearchBar {

        val state = State()
        var queryDebouncer: Debouncer<String>? = null

        inner class State(
            var query: UpdatableState<String> = UpdatableState(""),
            val searchTips: Loadable<List<String>> = Loadable(emptyList<String>())
        )


        fun ClickToCategoryUseCase(category: Category) {
            recordScenarioStep()

            scope.launchWithHandler {
                searchSettingsPanel.state.selectedCategory.post(category)
                loadAds()
            }
        }

        fun ClickToFilterButtonUseCase() {
            recordScenarioStep()

            searchSettingsPanel.state.enabled.inverse()
        }

        fun ChangeSearchQueryUseCase(newQuery: String) {
            recordScenarioStep(newQuery)

            state.query.post(newQuery)

            if (queryDebouncer == null) {
                queryDebouncer = Debouncer(newQuery) { lastQuery ->
                    scope.launchWithHandler {
                        if (lastQuery.isEmpty()) {
                            state.searchTips.loading.post(false)
                            state.searchTips.data.post(emptyList())
                            return@launchWithHandler
                        }

                        state.searchTips.loading.post(true)

                        val result = sources.backend.adsService.getSearchTips(
                            categoryId = searchSettingsPanel.state.selectedCategory.value?.id
                                ?: 0,
                            query = searchBar.state.query.value
                        )
                        state.searchTips.loading.post(false)
                        state.searchTips.data.post(result.getOrNull() ?: emptyList())
                    }
                }

            } else {
                queryDebouncer?.set(newQuery)
            }
        }

        fun SelectSearchTipUseCase() {
            recordScenarioStep()
        }

        suspend fun ClickToClearQueryUseCase() {
            recordScenarioStep()

            ChangeSearchQueryUseCase("")
        }

        suspend fun ClickToSearchUseCase() {
            recordScenarioStep()

            loadAds()
        }
    }

    inner class SearchSettingsPanel {
        val state = State()

        inner class State(
            var enabled: UpdatableState<Boolean> = UpdatableState(false),
            val categories: Loadable<List<Category>> = Loadable(emptyList<Category>()),
            var selectedCategory: UpdatableState<Category?> = UpdatableState(null),
            val regions: Loadable<List<Region>> = Loadable(emptyList<Region>()),
            var selectedRegion: UpdatableState<Region?> = UpdatableState(null),
            var priceRange: UpdatableState<PriceRange> = UpdatableState(PriceRange()),
            var categoryMenuEnabled: UpdatableState<Boolean> = UpdatableState(false),
            var regionMenuEnabled: UpdatableState<Boolean> = UpdatableState(false),
        )

        fun ChangePriceFromUseCase(value: Int) {
            recordScenarioStep(value)

            state.priceRange.post(state.priceRange.value.copy(from = value))
        }

        fun ChangePriceToUseCase(value: Int) {
            recordScenarioStep(value)

            state.priceRange.post(state.priceRange.value.copy(to = value))
        }

        fun ClickToCategoryUseCase() {
            recordScenarioStep()

            state.categoryMenuEnabled.post(true)
        }

        fun ClickToRegionUseCase() {
            recordScenarioStep()

            state.regionMenuEnabled.post(true)
        }

    }
}