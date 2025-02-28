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
        var adsPage = UpdatableState(0)
        var isCategoriesVisible = UpdatableState(false)
    }

    val searchBar = SearchBar()
    val searchSettingsPanel = SearchSettingsPanel()

    fun loadAds(resetPage: Boolean) {
        log("loadAds")
        state.ads.working.repostTo(get.sources().app.rootScreen.state.loadingEnabled)
        state.ads.act {
            val result = get.sources().backend.adsService.getAds(
                query = searchBar.state.selectedQuery.value,
                categoryId = searchSettingsPanel.state.selectedCategory.value?.id,
                range = searchSettingsPanel.state.priceRange.value,
                regionId = searchSettingsPanel.state.selectedRegion.value?.id,
                resetPage = resetPage
            )

            val newAds = result.getOrNull()

            val list = if (!resetPage) state.ads.output.value + (newAds ?: emptyList()) else newAds
            return@act Pair(list, result.isSuccess)
        }
    }

    inline fun loadCategories(crossinline onDone: () -> Unit) {
        searchSettingsPanel.state.categories.act {
            val result = get.sources().backend.adsService.getCategories()
            val categories = result.getOrNull() ?: emptyList()

            val selectedCategoryId = get.sources().platform.appDataStore.loadCategoryId()
            withContext(Dispatchers.Main) {
                selectedCategoryId?.let { selected ->
                    categories.firstOrNull { it.id == selected }?.let {
                        searchSettingsPanel.state.selectedCategory.next(it)
                        log("selectedCategory: ${searchSettingsPanel.state.selectedCategory.value}")
                    }
                }

                onDone()
            }
            return@act Pair(categories, result.isSuccess)
        }
    }

    fun StartScreenUseCase() {
        recordScenarioStep()

        val ads = state.ads.output.value
        val needToInit = ads.isEmpty()
        if (needToInit) {
            loadCategories {
                state.isCategoriesVisible.next(true)
                loadAds(true)
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

        if (!state.ads.output.value.isEmpty()) {
            state.adsPage.next(state.adsPage.value + 1)
            loadAds(false)
        }
    }

    fun CloseSearchSettingsPanelUseCase() {
        recordScenarioStep()

        searchSettingsPanel.state.enabled.next(false)
        loadAds(true)
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
            ChangeSearchQueryUseCase("")
            state.selectedQuery.next(selectedQuery)

            work {
                get.sources().backend.adsService.postTip(selectedQuery)
            }
            loadAds(true)
        }

        fun ClickToCategoryUseCase(category: Category) {
            recordScenarioStep()

            work {
                get.sources().platform.appDataStore.saveCategoryId(category.id)
            }
            searchSettingsPanel.state.selectedCategory.next(category)
            log("selectedCategory: ${searchSettingsPanel.state.selectedCategory.value}")
            loadAds(true)
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
            loadAds(true)
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
            var priceRange: UpdatableState<PriceRange> = UpdatableState(PriceRange()),
            var categoryMenuEnabled: UpdatableState<Boolean> = UpdatableState(false),
            var regionMenuEnabled: UpdatableState<Boolean> = UpdatableState(false),
        )

        fun ChangePriceFromUseCase(value: Int) {
            recordScenarioStep(value)

            state.priceRange.next(state.priceRange.value.copy(from = value))
        }

        fun ChangePriceToUseCase(value: Int) {
            recordScenarioStep(value)

            state.priceRange.next(state.priceRange.value.copy(to = value))
        }

        fun ClickToCategoryUseCase() {
            recordScenarioStep()

            state.categoryMenuEnabled.next(true)
        }

        fun ClickToRegionUseCase() {
            recordScenarioStep()

            state.regionMenuEnabled.next(true)
        }
    }
}