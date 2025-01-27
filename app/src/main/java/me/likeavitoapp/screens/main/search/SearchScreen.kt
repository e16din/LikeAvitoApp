package me.likeavitoapp.screens.main.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import me.likeavitoapp.Ad
import me.likeavitoapp.Category
import me.likeavitoapp.DataSources
import me.likeavitoapp.Loadable
import me.likeavitoapp.Screen
import me.likeavitoapp.SearchSettings
import me.likeavitoapp.dataSources
import me.likeavitoapp.screens.addetails.AdDetailsScreen
import kotlin.collections.emptyList
import kotlin.collections.plusAssign

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

    suspend fun ReloadDataUseCase() {
        state.ads.loading = true

        GetCategoriesUseCase()

        state.searchFilter.query = ""
        state.adsPage = 0

        GetAdsUseCase(state.searchFilter.query, state.adsPage)

        state.ads.loading = false
    }

    suspend fun GetCategoriesUseCase() {
        state.categories.loading = true

        val result = sources.backend.adsService.getCategories()
        val categories = result.getOrNull()
        state.categories.loading = false

        if (categories != null) {
            state.categories.data = categories
        } else {
            state.categories.loadingFailed = true
        }
    }

    fun GetAdDetailsUseCase(ad: Ad) {
        sources.app.currentScreen = AdDetailsScreen(
            ad = ad,
            prevScreen = sources.app.currentScreen
        )
    }

    suspend fun GetAdsNextPageUseCase() {
        GetAdsUseCase(state.searchFilter.query, state.adsPage+1)
        state.adsPage += 1
    }


    suspend fun GetAdsUseCase(query: String = "", page: Int){
        if (state.ads.loading) {
            return
        }

        val result = sources.backend.adsService.getAds(page = state.adsPage, query = query)
        val adsList = result.getOrNull()

        if (result.isSuccess && adsList != null) {
            state.ads.data = adsList.ads
        } else {
            state.ads.loadingFailed = true
        }
    }
}