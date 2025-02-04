package me.likeavitoapp.screens.main.tabs.search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
        val ads: Loadable<List<Ad>> = Loadable(emptyList<Ad>()),
        var adsPage: MutableState<Int> = mutableStateOf(0)
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
                state.ads.data.setUi(newAds)
            }
        )
    }

    suspend fun loadCategories() = with(searchSettingsPanel.state) {
        categories.load(
            loading = {
                return@load sources.backend.adsService.getCategories()
            },
            onSuccess = { newCategories ->
                categories.data.setUi(newCategories)
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

    fun CloseSearchSettingsPanelUseCase() {
        recordScenarioStep()

        scope.launchWithHandler {
            searchSettingsPanel.state.enabled.setUi(false)
            loadAds()
        }
    }


    inner class SearchBar {

        val state = State()
        var queryDebouncer: Debouncer<String>? = null

        inner class State(
            var query: MutableState<String> = mutableStateOf(""),
            val searchTips: Loadable<List<String>> = Loadable(emptyList<String>())
        )


        fun ClickToCategoryUseCase(category: Category) {
            recordScenarioStep()

            scope.launchWithHandler {
                searchSettingsPanel.state.selectedCategory.setUi(category)
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
                                searchTips.loading.setUi(false)
                                searchTips.data.setUi(emptyList())
                                return@with
                            }

                            searchTips.loading.setUi(true)

                            val result = sources.backend.adsService.getSearchTips(
                                categoryId = searchSettingsPanel.state.selectedCategory.value?.id
                                    ?: 0,
                                query = searchBar.state.query.value
                            )
                            searchTips.loading.setUi(false)
                            searchTips.data.setUi(result.getOrNull() ?: emptyList())
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
            var enabled: MutableState<Boolean> = mutableStateOf(false),
            val categories: Loadable<List<Category>> = Loadable(emptyList<Category>()),
            var selectedCategory: MutableState<Category?> = mutableStateOf(null),
            val regions: Loadable<List<Region>> = Loadable(emptyList<Region>()),
            var selectedRegion: MutableState<Region?> = mutableStateOf(null),
            var priceRange: MutableState<PriceRange> = mutableStateOf(PriceRange()),
            var categoryMenuEnabled: MutableState<Boolean> = mutableStateOf(false),
            var regionMenuEnabled: MutableState<Boolean> = mutableStateOf(false),
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