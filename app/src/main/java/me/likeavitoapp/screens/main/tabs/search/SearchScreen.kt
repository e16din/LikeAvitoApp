package me.likeavitoapp.screens.main.tabs.search

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.likeavitoapp.developer.primitives.Debouncer
import me.likeavitoapp.developer.primitives.work
import me.likeavitoapp.inverse
import me.likeavitoapp.log
import me.likeavitoapp.get
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.Category
import me.likeavitoapp.model.PriceRange
import me.likeavitoapp.model.Region
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.Worker
import me.likeavitoapp.model.act
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.addetails.AdDetailsScreen
import me.likeavitoapp.screens.main.tabs.BaseAdContainerScreen


class SearchScreen(
    override val navigator: ScreensNavigator,
    override val state: State = State()
) : BaseAdContainerScreen(navigator, state) {

    class State() : BaseAdContainerState() {
        val ads = Worker<List<Ad>>(mutableListOf<Ad>())
        var isCategoriesVisible = UpdatableState(false)
        var isSearchSettingsVisible = UpdatableState(false)
        var pullToRefreshEnabled = UpdatableState(false)
    }

    val searchBar = SearchBar()
    val searchSettingsPanel = SearchSettingsPanel()

    fun loadAds(resetPage: Boolean, afterAll: () -> Unit = {}) {
        log("loadAds")
        state.ads.working.repostTo(get.sources().app.rootScreen.state.loadingEnabled)
        state.ads.act(onDone = { afterAll() }) {
            val from = if(searchSettingsPanel.state.priceFrom.value.isEmpty())
                0
            else
                searchSettingsPanel.state.priceFrom.value.toInt()

            val to = if(searchSettingsPanel.state.priceTo.value.isEmpty())
                0
            else
                searchSettingsPanel.state.priceTo.value.toInt()

            val result = get.sources().backend.adsService.getAds(
                query = searchBar.state.selectedQuery.value,
                categoryId = searchSettingsPanel.state.selectedCategory.value?.id,
                range = PriceRange(from, to),
                regionId = searchSettingsPanel.state.selectedRegion.value?.id,
                resetPage = resetPage
            )

            val newAds = result.getOrNull()

            val list = if (!resetPage) state.ads.output.value + (newAds ?: emptyList()) else newAds
            return@act Pair(list, result.isSuccess)
        }
    }

    fun loadRegions(onDone: (List<Region>) -> Unit) {
        searchSettingsPanel.state.regions.act {
            val result = get.sources().backend.adsService.getRegions()
            val regions = result.getOrNull() ?: emptyList()

            val selectedRegionId = get.sources().platform.appDataStore.loadRegionId()
            withContext(Dispatchers.Main) {
                selectedRegionId?.let { selected ->
                    regions.firstOrNull { it.id == selected }?.let {
                        searchSettingsPanel.state.selectedRegion.next(it)
                    }
                }

                onDone(regions)
            }
            return@act Pair(regions, result.isSuccess)
        }
    }

    fun loadCategories(onDone: (List<Category>) -> Unit) {
        searchSettingsPanel.state.categories.act {
            val result = get.sources().backend.adsService.getCategories()
            val categories = result.getOrNull() ?: emptyList()

            val selectedCategoryId = get.sources().platform.appDataStore.loadCategoryId()
            withContext(Dispatchers.Main) {
                selectedCategoryId?.let { selected ->
                    categories.firstOrNull { it.id == selected }?.let {
                        searchSettingsPanel.state.selectedCategory.next(it)
                    }
                }

                onDone(categories)
            }
            return@act Pair(categories, result.isSuccess)
        }
    }

    fun StartScreenUseCase() {
        recordScenarioStep()

        val ads = state.ads.output.value
        val needToInit = ads.isEmpty()
        if (needToInit) {
            loadCategories { categories ->
                if (searchSettingsPanel.state.selectedCategory.value == null) {
                    searchSettingsPanel.state.selectedCategory.next(categories.first())
                }
                state.isCategoriesVisible.next(true)

                loadRegions { regions ->
                    if (searchSettingsPanel.state.selectedRegion.value == null) {
                        searchSettingsPanel.state.selectedRegion.next(regions.first())
                    }
                    state.isSearchSettingsVisible.next(true)
                }

                loadAds(resetPage = true)
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

        navigator.startScreen(
            AdDetailsScreen(ad, navigator)
        )
    }

    fun ScrollToEndUseCase() {
        recordScenarioStep()

        loadAds(resetPage = false)
    }

    fun CloseSearchSettingsPanelUseCase() {
        recordScenarioStep()

        searchSettingsPanel.state.enabled.next(false)
        loadAds(resetPage = true)
    }

    fun PullToRefreshUseCase() {
        recordScenarioStep()

        state.pullToRefreshEnabled.next(true)
        loadAds(resetPage = true) {
            state.pullToRefreshEnabled.next(false)
        }
    }

    inner class SearchBar {

        val state = State()
        var queryDebouncer: Debouncer<String>? = null

        inner class State(
            var query: UpdatableState<String> = UpdatableState(""),
            var selectedQuery: UpdatableState<String?> = UpdatableState(null),
            val searchTips: Worker<List<String>> = Worker(emptyList<String>())
        )

        fun search(selectedQuery: String) {
            log("search")
            ChangeSearchQueryUseCase("")
            state.selectedQuery.next(selectedQuery)

            work {
                get.sources().backend.adsService.postTip(selectedQuery)
            }
            loadAds(resetPage = true)
        }

        fun ClickToCategoryUseCase(category: Category) {
            recordScenarioStep(category)

            work {
                get.sources().platform.appDataStore.saveCategoryId(category.id)
            }
            searchSettingsPanel.state.selectedCategory.next(category)
            loadAds(resetPage = true)
        }

        fun ClickToFilterButtonUseCase() {
            recordScenarioStep()

            searchSettingsPanel.state.enabled.inverse()
        }

        fun ChangeSearchQueryUseCase(newQuery: String) {
            recordScenarioStep(newQuery)

            state.query.next(newQuery)


            if (queryDebouncer == null) {
                queryDebouncer = Debouncer(newQuery) { lastQuery ->
                    if (lastQuery.isEmpty()) {
                        state.searchTips.resetWith(emptyList())
                        return@Debouncer
                    }
                    state.searchTips.act {
                        val result = get.sources().backend.adsService.getSearchTips(
                            query = searchBar.state.query.value
                        )

                        return@act Pair(result.getOrNull(), result.isSuccess)
                    }
                }

            } else {
                queryDebouncer?.set(newQuery)
            }
        }

        fun ClickToSearchTipUseCase(tip: String) {
            recordScenarioStep()

            search(tip)
        }

        fun ClickToClearUseCase() {
            recordScenarioStep()

            ChangeSearchQueryUseCase("")
        }

        fun ClickToTipsBackUseCase() {
            recordScenarioStep()

            ChangeSearchQueryUseCase("")
        }

        fun ClickToSelectedCategoryUseCase() {
            recordScenarioStep()

            searchSettingsPanel.state.selectedCategory.next(null)
        }

        fun ClickToSelectedQueryUseCase() {
            recordScenarioStep()

            state.selectedQuery.next(null)
            ChangeSearchQueryUseCase("")
            loadAds(resetPage = true)
        }

        fun ClickToSearchActionUseCase(query: String) {
            recordScenarioStep()

            search(query)
        }
    }

    inner class SearchSettingsPanel {
        val state = State()

        inner class State(
            var enabled: UpdatableState<Boolean> = UpdatableState(false),
            val categories: Worker<List<Category>> = Worker(emptyList<Category>()),
            var selectedCategory: UpdatableState<Category?> = UpdatableState(null),
            val regions: Worker<List<Region>> = Worker(emptyList<Region>()),
            var selectedRegion: UpdatableState<Region?> = UpdatableState(null),
            var priceFrom: UpdatableState<String> = UpdatableState(""),
            var priceTo: UpdatableState<String> = UpdatableState(""),
            var categoryMenuEnabled: UpdatableState<Boolean> = UpdatableState(false),
            var regionMenuEnabled: UpdatableState<Boolean> = UpdatableState(false),
        )

        fun ChangePriceFromUseCase(value: String) {
            recordScenarioStep(value)

            state.priceFrom.next(value)
        }

        fun ChangePriceToUseCase(value: String) {
            recordScenarioStep(value)

            state.priceTo.next(value)
        }

        fun ClickToCategoryUseCase() {
            recordScenarioStep()

            state.categoryMenuEnabled.next(true)
        }

        fun ClickToRegionUseCase() {
            recordScenarioStep()

            state.regionMenuEnabled.next(true)
        }

        fun DismissCategoryMenuUseCase() {
            recordScenarioStep()

            state.categoryMenuEnabled.next(false)
        }

        fun ChangeCategoryUseCase(category: Category) {
            recordScenarioStep(category)

            work {
                get.sources().platform.appDataStore.saveCategoryId(category.id)
            }
            state.selectedCategory.next(category)
            state.categoryMenuEnabled.next(false)
        }

        fun DismissRegionMenuUseCase() {
            recordScenarioStep()

            state.regionMenuEnabled.next(false)
        }

        fun ChangeRegionUseCase(region: Region) {
            recordScenarioStep(region)

            work {
                get.sources().platform.appDataStore.saveCategoryId(region.id)
            }
            state.selectedRegion.next(region)
            state.regionMenuEnabled.next(false)
        }
    }
}