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
            val from = if (searchSettingsPanel.state.priceFrom.value.isEmpty())
                0
            else
                searchSettingsPanel.state.priceFrom.value.toInt()

            val to = if (searchSettingsPanel.state.priceTo.value.isEmpty())
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

    fun StartScreenUseCase() {
        recordScenarioStep()

        val ads = state.ads.output.value
        val needToInit = ads.isEmpty()
        if (needToInit) {
            work {
                val platform = get.sources().platform
                val selectedCategoryId = platform.appDataStore.loadCategoryId()
                withContext(Dispatchers.Main) {
                    val categories = get.sources().app.categories
                    selectedCategoryId?.let { selected ->
                        categories.firstOrNull { it.id == selected }?.let {
                            searchSettingsPanel.state.selectedCategory.next(it)
                        }
                    }
                    state.isCategoriesVisible.next(true)
                }

                val selectedRegionId = platform.appDataStore.loadRegionId()
                withContext(Dispatchers.Main) {
                    selectedRegionId?.let { selected ->
                        get.sources().app.regions.firstOrNull { it.id == selected }?.let {
                            searchSettingsPanel.state.selectedRegion.next(it)
                        }
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
            var categories: List<Category> = get.sources().app.categories,
            val regions: List<Region> = get.sources().app.regions,
            var enabled: UpdatableState<Boolean> = UpdatableState(false),
            var selectedCategory: UpdatableState<Category?> = UpdatableState(null),
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
                get.sources().platform.appDataStore.saveRegionId(region.id)
            }
            state.selectedRegion.next(region)
            state.regionMenuEnabled.next(false)
        }
    }
}