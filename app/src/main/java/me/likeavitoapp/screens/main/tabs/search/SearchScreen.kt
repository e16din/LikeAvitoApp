package me.likeavitoapp.screens.main.tabs.search

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import me.likeavitoapp.actualScope
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.Category
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.PriceRange
import me.likeavitoapp.model.Region
import me.likeavitoapp.model.dataSources
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.inverse
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.screens.main.addetails.AdDetailsScreen


class SearchScreen(
    val parentNavigator: ScreensNavigator,
    val scope: CoroutineScope = actualScope,
    val sources: DataSources = dataSources(),
) : IScreen {

    class State(
        val ads: Loadable<List<Ad>> = Loadable(emptyList<Ad>()),
        var adsPage: MutableStateFlow<Int> = MutableStateFlow(0)
    )

    val state = State()
    lateinit var navigator: ScreensNavigator

    val searchBar = SearchBar()
    val searchSettingsPanel = SearchSettingsPanel()


    suspend fun loadAds() {
        with(state) {
            ads.loading.value = true

            val result = sources.backend.adsService.getAds(
                page = adsPage.value,
                query = searchBar.state.query.value,
                categoryId = searchSettingsPanel.state.selectedCategory.value?.id ?: 0,
                range = searchSettingsPanel.state.priceRange.value,
                regionId = searchSettingsPanel.state.selectedRegion.value?.id ?: 0
            )
            val newAds = result.getOrNull()

            ads.loading.value = false

            if (result.isSuccess && newAds != null) {
                ads.data.value = newAds
            } else {
                ads.loadingFailed.value = true
            }
        }
    }


    private val loadCategoriesCalls = MutableStateFlow(Unit)
    fun loadCategories() = with(state) {
        loadCategoriesCalls.value = Unit
    }

    suspend fun listenLoadCategoriesCalls() {
        loadCategoriesCalls.collect {
            with(searchSettingsPanel.state) {
                categories.loading.value = true

                val result = sources.backend.adsService.getCategories()
                val newCategories = result.getOrNull()
                categories.loading.value = false

                if (newCategories != null) {
                    categories.data.value = newCategories
                } else {
                    categories.loadingFailed.value = true
                }
            }
        }
    }

    suspend fun StartScreenUseCase() = with(state) {
        recordScenarioStep()

        loadCategories()
        loadAds()
    }

    fun CloseSearchSettingsPanelUseCase() {
        recordScenarioStep()

        scope.launchWithHandler {
            searchSettingsPanel.state.enabled.value = false
            loadAds()
        }
    }

    inner class SearchBar {

        val state = State()

        inner class State(
            var query: MutableStateFlow<String> = MutableStateFlow(""),
            val searchTips: Loadable<List<String>> = Loadable(emptyList<String>())
        )

        fun ClickToCategoryUseCase(category: Category) {
            recordScenarioStep()

            scope.launchWithHandler {
                searchSettingsPanel.state.selectedCategory.value = category
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
        }

        suspend fun listenChangeSearchQueryCalls() = with(state) {
            searchBar.state.query.debounce(390).collect { lastQuery ->
                if (lastQuery.isEmpty()) {
                    searchTips.loading.value = false
                    searchTips.data.value = emptyList()
                    return@collect
                }

                searchTips.loading.value = true

                val result = sources.backend.adsService.getSearchTips(
                    categoryId = searchSettingsPanel.state.selectedCategory.value?.id ?: 0,
                    query = searchBar.state.query.value
                )
                searchTips.loading.value = false
                searchTips.data.value = result.getOrNull() ?: emptyList()
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
            var enabled: MutableStateFlow<Boolean> = MutableStateFlow(false),
            val categories: Loadable<List<Category>> = Loadable(emptyList<Category>()),
            var selectedCategory: MutableStateFlow<Category?> = MutableStateFlow(null),
            val regions: Loadable<List<Region>> = Loadable(emptyList<Region>()),
            var selectedRegion: MutableStateFlow<Region?> = MutableStateFlow(null),
            var priceRange: MutableStateFlow<PriceRange> = MutableStateFlow(PriceRange()),
            var categoryMenuEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false),
            var regionMenuEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false),
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

    fun ClickToAdUseCase(ad: Ad) {
        recordScenarioStep()

        parentNavigator.startScreen(AdDetailsScreen(ad, parentNavigator))
    }

    suspend fun ScrollToEndUseCase() {
        if (state.ads.data.value.isEmpty()) {
            return
        }

        recordScenarioStep()

        state.adsPage.value += 1
        loadAds()
    }

    fun ClickToFavoriteUseCase(ad: Ad) {
        recordScenarioStep()

        scope.launchWithHandler {
            ad.isFavorite.inverse()
            sources.backend.adsService.updateFavoriteState(ad)
        }
    }

    fun ClickToBuyUseCase() {


    }

    fun ClickToBargainingUseCase() {

    }

}