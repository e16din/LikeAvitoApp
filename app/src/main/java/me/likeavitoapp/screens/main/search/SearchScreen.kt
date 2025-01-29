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
import me.likeavitoapp.screens.addetails.AdDetailsScreen
import kotlin.collections.emptyList

class SearchScreen(
    val sources: DataSources = dataSources(),
    override var prevScreen: Screen? = null,
    override var innerScreen: MutableStateFlow<Screen>? = null,
) : Screen {

    val state: State = State()

    class State {
        val categories = Loadable(emptyList<Category>())
        val ads = Loadable(emptyList<Ad>())
        var adsPage = MutableStateFlow(0)
        var searchTips = Loadable(emptyList<String>())

        var searchFilter =
            SearchSettings(
                category = MutableStateFlow(Category(name = "", id = 0)),
                query = MutableStateFlow(""),
                region = MutableStateFlow(SearchSettings.Region(name = "Все регионы", id = 0)),
                priceRange = MutableStateFlow(SearchSettings.PriceRange(from = 0, to = Int.MAX_VALUE))
            )
    }

    // SearchBarUseCases:

    suspend fun ChangeSearchQueryUseCase(newQuery: String) = with(state.searchFilter) {
        query.value = newQuery

        if (query.subscriptionCount.value == 0) {
            query.debounce(390).collect { lastQuery ->
                    state.searchTips.loading.value = true

                    val result = sources.backend.adsService.getSearchTips(
                        categoryId = category.value.id,
                        query = newQuery
                    )
                    state.searchTips.loading.value = false
                    state.searchTips.data.value = result.getOrNull() ?: emptyList()
                }
        }
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

        searchFilter.query.value = ""
        adsPage.value = 0

        GetAdsUseCase(searchFilter.query.value, adsPage.value)

        ads.loading.value = false
    }

    suspend fun GetCategoriesUseCase()= with(state) {
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

    suspend fun ScrollToEndUseCase() {
        GetAdsUseCase(state.searchFilter.query.value, state.adsPage.value+1)
        state.adsPage.value += 1
    }


    suspend fun GetAdsUseCase(query: String = "", page: Int) = with(state){
        if (ads.loading.value) {
            return
        }

        val result = sources.backend.adsService.getAds(page = adsPage.value, query = query)
        val newAds = result.getOrNull()

        if (result.isSuccess && newAds != null) {
            ads.data.value = newAds.ads
        } else {
            ads.loadingFailed.value= true
        }
    }

    suspend fun ClickToClearQueryUseCase() {
        ChangeSearchQueryUseCase("")
    }

    suspend fun ClickToSearchUseCase() = with(state.searchFilter) {
        GetAdsUseCase(query.value, 0)
    }
}