package me.likeavitoapp.screens.main.tabs.search

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.Debouncer
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
import me.likeavitoapp.model.StateValue
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.tabs.BaseAdScreen
import me.likeavitoapp.setUi


class SearchScreen(
    override val parentNavigator: ScreensNavigator,
    override val scope: CoroutineScope = provideCoroutineScope(),
    override val sources: DataSources = provideDataSources()
) : BaseAdScreen(parentNavigator, scope, sources) {

    class State(
        val ads: Loadable<SnapshotStateList<Ad>> = Loadable(mutableStateListOf<Ad>()),
        var adsPage: StateValue<Int> = StateValue(0)
    ) : BaseAdState()

    override val state = State()

    lateinit var navigator: ScreensNavigator

    val searchBar = SearchBar()
    val searchSettingsPanel = SearchSettingsPanel()

    fun ScrollToEndUseCase() {
        recordScenarioStep()

        if (!state.ads.data.value.isEmpty()) {
            scope.launchWithHandler {
                state.adsPage.value += 1
                loadAds()
            }
        }
    }

    suspend fun loadAds() {
        log("loadAds")
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
                val list = newAds.toMutableStateList()
                sources.app.ads = list
                state.ads.data.set(list)
            }
        )
    }

    suspend fun loadCategories() = with(searchSettingsPanel.state) {
        categories.load(
            loading = {
                return@load sources.backend.adsService.getCategories()
            },
            onSuccess = { newCategories ->
                categories.data.set(newCategories)
            }
        )
    }

    fun StartScreenUseCase() {
        recordScenarioStep()

        scope.launchWithHandler {
            loadCategories()
            loadAds()
        }
    }

    override fun ClickToFavoriteUseCase(ad: Ad) {
        super.ClickToFavoriteUseCase(ad)

        val i = state.ads.data.value.indexOf(ad)
        state.ads.data.value = state.ads.data.value.toMutableStateList()
    }

    fun CloseSearchSettingsPanelUseCase() {
        recordScenarioStep()

        scope.launchWithHandler {
            searchSettingsPanel.state.enabled.set(false)
            loadAds()
        }
    }


    inner class SearchBar {

        val state = State()
        var queryDebouncer: Debouncer<String>? = null

        inner class State(
            var query: StateValue<String> = StateValue(""),
            val searchTips: Loadable<List<String>> = Loadable(emptyList<String>())
        )


        fun ClickToCategoryUseCase(category: Category) {
            recordScenarioStep()

            scope.launchWithHandler {
                searchSettingsPanel.state.selectedCategory.set(category)
                loadAds()
            }
        }

        fun ClickToFilterButtonUseCase() {
            recordScenarioStep()

            searchSettingsPanel.state.enabled.inverse()
        }

        fun ChangeSearchQueryUseCase(newQuery: String) {
            recordScenarioStep(newQuery)

            state.query.value = newQuery

            if (queryDebouncer == null) {
                queryDebouncer = Debouncer(newQuery) { lastQuery ->
                    scope.launchWithHandler {
                        with(state) {
                            if (lastQuery.isEmpty()) {
                                searchTips.loading.set(false)
                                searchTips.data.set(emptyList())
                                return@with
                            }

                            searchTips.loading.set(true)

                            val result = sources.backend.adsService.getSearchTips(
                                categoryId = searchSettingsPanel.state.selectedCategory.value?.id
                                    ?: 0,
                                query = searchBar.state.query.value
                            )
                            searchTips.loading.set(false)
                            searchTips.data.set(result.getOrNull() ?: emptyList())
                        }
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
            var enabled: StateValue<Boolean> = StateValue(false),
            val categories: Loadable<List<Category>> = Loadable(emptyList<Category>()),
            var selectedCategory: StateValue<Category?> = StateValue(null),
            val regions: Loadable<List<Region>> = Loadable(emptyList<Region>()),
            var selectedRegion: StateValue<Region?> = StateValue(null),
            var priceRange: StateValue<PriceRange> = StateValue(PriceRange()),
            var categoryMenuEnabled: StateValue<Boolean> = StateValue(false),
            var regionMenuEnabled: StateValue<Boolean> = StateValue(false),
        )

        fun ChangePriceFromUseCase(value: Int) {
            recordScenarioStep(value)

            state.priceRange.value = state.priceRange.value.copy(from = value)
        }

        fun ChangePriceToUseCase(value: Int) {
            recordScenarioStep(value)

            state.priceRange.value = state.priceRange.value.copy(to = value)
        }

        fun ClickToCategoryUseCase() {
            recordScenarioStep()

            state.categoryMenuEnabled.value = true
        }

        fun ClickToRegionUseCase() {
            recordScenarioStep()

            state.regionMenuEnabled.value = true
        }

    }
}