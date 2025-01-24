package me.likeavitoapp.screens.main.search

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import me.likeavitoapp.Ad
import me.likeavitoapp.DataSources
import me.likeavitoapp.exceptionHandler
import me.likeavitoapp.screens.addetails.AdDetailsScreen


class ChangeSelectedCategoryUseCase()
class ChangeSearchFilterUseCase()

class GetCategoriesUseCase(
    val scope: CoroutineScope,
    val sources: DataSources<SearchScreen>) {

    fun run(): Job? {
        return scope.launch {
            sources.backend.adsService.getCategories()
        }
    }
}
class ReloadUseCase(
    val scope: CoroutineScope,
    val sources: DataSources<SearchScreen>) {
    inline fun runWith(crossinline loadData: suspend ()->Unit) = with(sources.screen.state) {
        adsLoading = true
        scope.launch {
            loadData()
            adsLoading = false
        }
    }
}

class AdDetailsUseCase(
    val sources: DataSources<SearchScreen>
) {
    fun runWith(ad: Ad) {
        sources.app.currentScreenFlow.tryEmit(
            AdDetailsScreen(ad)
        )
    }
}

class GetAdsUseCase(
    val scope: CoroutineScope,
    val sources: DataSources<SearchScreen>
) {
    var page = 0
    var lastQuery = ""

    fun runWith(query: String = lastQuery): Job? = with(sources.screen.state) {
        if (sources.screen.state.adsLoading) {
            return@with null
        }

        if (lastQuery != query) {
            page = 0
        }
        lastQuery = query

       return@with scope.launch(exceptionHandler) {
            val result = sources.backend.adsService.getAds(page = page, query = query)
            val adsList = result.getOrNull()

            if (result.isSuccess && adsList != null) {
                ads = adsList.ads
            } else {
                adsLoadingError = true
            }
        }
    }
}

class ChangeSearchQueryUseCase(
    val scope: CoroutineScope,
    val sources: DataSources<SearchScreen>
) {

    var queryFlow: MutableStateFlow<String>? = null

    fun runWith(newQuery: String) = with(sources.screen.state) {
        searchFilter.query = newQuery

        if (queryFlow == null) {
            queryFlow = MutableStateFlow(newQuery)
            scope.launch {
                queryFlow?.debounce(390)?.collect { lastQuery ->
                    tipsLoading = true
                    val result = sources.backend.adsService.getSearchTips(
                        categoryId = selectedCategory.id,
                        query = lastQuery
                    )
                    tipsLoading = false
                    searchTips = result.getOrNull() ?: emptyList()
                }
            }
        } else {
            queryFlow?.tryEmit(newQuery)
        }
    }
}