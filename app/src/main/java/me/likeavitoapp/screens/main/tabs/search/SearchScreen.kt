package me.likeavitoapp.screens.main.tabs.search

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import me.likeavitoapp.bindScenarioDataSource
import me.likeavitoapp.developer.primitives.Debouncer
import me.likeavitoapp.inverse
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
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

    class State(
        val ads: Worker<SnapshotStateList<Ad>> = Worker(mutableStateListOf<Ad>()),
        var adsPage: UpdatableState<Int> = UpdatableState(0)
    ) : BaseAdContainerState()

    val searchBar = SearchBar()
    val searchSettingsPanel = SearchSettingsPanel()

    suspend fun loadAds() {
        log("loadAds")
        state.ads.working.repostTo(get.sources().app.rootScreen.state.loadingEnabled)
        state.ads.load(
            loading = {
                return@load get.sources().backend.adsService.getAds(
                    page = state.adsPage.value,
                    query = searchBar.state.query.value,
                    categoryId = searchSettingsPanel.state.selectedCategory.value?.id ?: 0,
                    range = searchSettingsPanel.state.priceRange.value,
                    regionId = searchSettingsPanel.state.selectedRegion.value?.id ?: 0
                )
            },
            onSuccess = { newAds ->
                with(newAds.toMutableStateList()) {
                    state.ads.output.post(this)
                    bindScenarioDataSource(Ad::class, this)
                }
            }
        )
    }

    suspend fun loadCategories() = with(searchSettingsPanel) {
        state.categories.act {
            val result = get.sources().backend.adsService.getCategories()
            val categories = result.getOrNull() ?: emptyList()

            val selectedCategoryId = get.sources().platform.appDataStore.loadCategoryId()
            selectedCategoryId?.let { selected ->
                categories.firstOrNull { it.id == selected }?.let {
                    searchSettingsPanel.state.selectedCategory.post(it)
                }
            }

            return@act Pair(categories, result.isSuccess)
        }
    }

    fun StartScreenUseCase() {
        recordScenarioStep()

        val ads = state.ads.output.value
        val needToInit = ads.isEmpty()
        if (needToInit) {
            get.scope().launchWithHandler {
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

        navigator.startScreen(
            AdDetailsScreen(ad, navigator)
        )
    }

    fun ScrollToEndUseCase() {
        recordScenarioStep()

        if (!state.ads.output.value.isEmpty()) {
            get.scope().launchWithHandler {
                state.adsPage.post(state.adsPage.value + 1)
                loadAds()
            }
        }
    }

    fun CloseSearchSettingsPanelUseCase() {
        recordScenarioStep()

        get.scope().launchWithHandler {
            searchSettingsPanel.state.enabled.post(false)
            loadAds()
        }
    }

    inner class SearchBar {

        val state = State()
        var queryDebouncer: Debouncer<String>? = null

        inner class State(
            var query: UpdatableState<String> = UpdatableState(""),
            val searchTips: Worker<List<String>> = Worker(emptyList<String>())
        )


        fun ClickToCategoryUseCase(category: Category) {
            recordScenarioStep()

            get.scope().launchWithHandler {
                with(get.sources()) {
                    platform.appDataStore.saveCategoryId(category.id)
                }

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
                    if (lastQuery.isEmpty()) {
                        state.searchTips.resetWith(emptyList())
                        return@Debouncer
                    }

                    get.scope().launchWithHandler {
                        state.searchTips.load(loading = {
                            get.sources().backend.adsService.getSearchTips(
                                categoryId = searchSettingsPanel.state.selectedCategory.value?.id
                                    ?: 0,
                                query = searchBar.state.query.value
                            )
                        }, onSuccess = { data ->
                            state.searchTips.output.post(data)
                        })
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

        fun ClickToTipsClearUseCase() {
            recordScenarioStep()

            ChangeSearchQueryUseCase("")
        }

        fun ClickToTipsBackUseCase() {
            recordScenarioStep()

            ChangeSearchQueryUseCase("")
        }

        fun ClickToSelectedCategoryUseCase() {
            recordScenarioStep()

            searchSettingsPanel.state.selectedCategory.post(null)
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