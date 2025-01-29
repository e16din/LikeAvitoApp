package me.likeavitoapp.screens.main.search

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import me.likeavitoapp.Ad
import me.likeavitoapp.Category
import me.likeavitoapp.DataSources
import me.likeavitoapp.Loadable
import me.likeavitoapp.Screen
import me.likeavitoapp.SearchSettings
import me.likeavitoapp.dataSources
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.addetails.AdDetailsScreen
import kotlin.collections.emptyList

class SearchScreen(
    val state: State = State(),
    val sources: DataSources = dataSources(),
    override var prevScreen: Screen? = null,
    override var innerScreen: MutableStateFlow<Screen>? = null,
) : Screen {


    class State(
        val categoriesEnabled: MutableStateFlow<Boolean> = MutableStateFlow(true),
        val categories: Loadable<List<Category>> = Loadable(emptyList<Category>()),
        val ads: Loadable<List<Ad>> = Loadable(emptyList<Ad>()),
        var adsPage: MutableStateFlow<Int> = MutableStateFlow(0),
        var searchTips: Loadable<List<String>> = Loadable(emptyList<String>()),
        var searchFilter: SearchSettings =
            SearchSettings(
                category = MutableStateFlow(Category(name = "", id = 0)),
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

    private val getAdsUseCaseFlow = MutableStateFlow(Unit)

    // SearchBarUseCases:

    fun ChangeSearchQueryUseCase(newQuery: String) {
        recordScenarioStep(newQuery)
        state.searchFilter.query.value = newQuery
    }

    suspend fun ListenChangeSearchQueryUseCase() = with(state.searchFilter) {
        query.debounce(390).collect { lastQuery ->
            state.searchTips.loading.value = true

            val result = sources.backend.adsService.getSearchTips(
                categoryId = category.value.id,
                query = query.value
            )
            state.searchTips.loading.value = false
            state.searchTips.data.value = result.getOrNull() ?: emptyList()
        }
    }

    fun ClickToFavoriteIconUseCase() {

    }

    fun SelectSearchTipUseCase() {

    }

    fun SelectCategoryUseCase() {

    }

    fun ChangeSearchFilterUseCase() {
    }

    // AdListUseCases:

    suspend fun ReloadDataUseCase() = with(state) {
        ads.loading.value = true

        GetCategoriesUseCase()

        adsPage.value = 0

        GetAdsUseCase()

        ads.loading.value = false
    }

    suspend fun GetCategoriesUseCase() = with(state) {
        categories.loading.value = true

        val result = sources.backend.adsService.getCategories()
        val newCategories = result.getOrNull()
        categories.loading.value = false

        if (newCategories != null) {
            state.categories.data.value = newCategories
        } else {
            state.categories.loadingFailed.value = true
        }
    }

    fun ClickToAdUseCase(ad: Ad) {
        sources.app.currentScreen.value = AdDetailsScreen(
            ad = ad,
            prevScreen = sources.app.currentScreen.value
        )
    }

    fun ScrollToEndUseCase() {
        state.adsPage.value += 1
        GetAdsUseCase()
    }

    private fun GetAdsUseCase() {
        getAdsUseCaseFlow.value = Unit
    }

    suspend fun ListenGetAdsUseCase() {
        getAdsUseCaseFlow.collect {
            with(state) {
                if (ads.loading.value) {
                    return@collect
                }

                val result = sources.backend.adsService.getAds(
                    page = adsPage.value,
                    query = searchFilter.query.value,
                    categoryId = searchFilter.category.value.id
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

    suspend fun ClickToClearQueryUseCase() {
        ChangeSearchQueryUseCase("")
    }

    suspend fun ClickToSearchUseCase() = with(state.searchFilter) {
        GetAdsUseCase()
    }

    fun ClickToCategoryUseCase(category: Category) {
        state.searchFilter.category.value = category
        GetAdsUseCase()
    }

    fun ClickToFilterButtonUseCase() {
        recordScenarioStep()
    }

}