package me.likeavitoapp.screens.main.search

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.Category
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.SearchSettings
import me.likeavitoapp.model.dataSources
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.addetails.AdDetailsScreen


class SearchScreen(
    val sources: DataSources = dataSources(),
    override var prevScreen: IScreen? = null,
    override var innerScreen: MutableStateFlow<IScreen>? = null,
) : IScreen {

    val state = State()

    val searchBar = SearchBarUseCases(sources)
    val adsList = AdsListUseCases(sources)

    private val loadAdsCalls = MutableStateFlow(Unit)
    fun loadAds() {
        loadAdsCalls.value = Unit
    }
    suspend fun listenLoadAdsCalls() {
        loadAdsCalls.collect {
            with(state) {
                if (ads.loading.value) {
                    return@collect
                }

                val result = sources.backend.adsService.getAds(
                    page = adsPage.value,
                    query = searchSettings.query.value,
                    categoryId = searchSettings.selectedCategory.value!!.id
                )
                val newAds = result.getOrNull()

                if (result.isSuccess && newAds != null) {
                    ads.data.value = newAds
                } else {
                    ads.loadingFailed.value = true
                }
            }
        }
    }

    private val loadCategoriesCalls = MutableStateFlow(Unit)
    fun loadCategories() = with(state) {
        loadCategoriesCalls.value = Unit
    }
    suspend fun listenLoadCategoriesCalls() {
        loadCategoriesCalls.collect {
            with(state.searchSettings) {
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

    class State(
        val ads: Loadable<List<Ad>> = Loadable(emptyList<Ad>()),
        var adsPage: MutableStateFlow<Int> = MutableStateFlow(0),
        var searchSettingsPanelEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false),
        var searchTips: Loadable<List<String>> = Loadable(emptyList<String>()),
        var searchSettings: SearchSettings =
            SearchSettings(
                categories = Loadable(emptyList<Category>()),
                selectedCategory = MutableStateFlow(null),
                query = MutableStateFlow(""),
                region = MutableStateFlow(SearchSettings.Region(name = "Все регионы", id = 0)),
                priceRange = MutableStateFlow(
                    SearchSettings.PriceRange(
                        from = 0,
                        to = Int.MAX_VALUE
                    )
                )
            )
    )

    fun StartScreenUseCase() = with(state) {
        recordScenarioStep()

        ads.loading.value = true

        loadCategories()

        adsPage.value = 0

        loadAds()

        ads.loading.value = false
    }

    fun DismissSearchSettingsPanelUseCase() {
        recordScenarioStep()

        state.searchSettingsPanelEnabled.value = false
    }

    inner class SearchBarUseCases(val sources: DataSources) {

        fun ClickToCategoryUseCase(category: Category) {
            recordScenarioStep()

            state.searchSettings.selectedCategory.value = category
            loadAds()
        }

        fun ClickToFilterButtonUseCase() {
            recordScenarioStep()

            state.searchSettingsPanelEnabled.value = !state.searchSettingsPanelEnabled.value
        }

        fun ChangeSearchQueryUseCase(newQuery: String) {
            recordScenarioStep(newQuery)

            state.searchSettings.query.value = newQuery
        }

        suspend fun listenChangeSearchQueryCalls() = with(state) {
            searchSettings.query.debounce(390).collect { lastQuery ->
                searchTips.loading.value = true

                val result = sources.backend.adsService.getSearchTips(
                    categoryId = searchSettings.selectedCategory.value!!.id,
                    query = searchSettings.query.value
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

        suspend fun ClickToSearchUseCase() = with(state.searchSettings) {
            recordScenarioStep()

            loadAds()
        }
    }

    inner class AdsListUseCases(val sources: DataSources) {

        fun ClickToAdUseCase(ad: Ad) {
            recordScenarioStep()

            sources.app.currentScreen.value = AdDetailsScreen(
                ad = ad,
                prevScreen = sources.app.currentScreen.value
            )
        }

        fun ScrollToEndUseCase() {
            recordScenarioStep()

            state.adsPage.value += 1
            loadAds()
        }

        fun ClickToFavoriteUseCase() {
            recordScenarioStep()
        }

        fun ClickToBuyUseCase() {


        }

        fun ClickToBargainingUseCase() {

        }
    }
}