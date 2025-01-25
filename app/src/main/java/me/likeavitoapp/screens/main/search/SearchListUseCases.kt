package me.likeavitoapp.screens.main.search

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.likeavitoapp.Ad
import me.likeavitoapp.DataSources
import me.likeavitoapp.UseCaseResult
import me.likeavitoapp.screens.addetails.AdDetailsScreen


fun ReloadDataUseCase(
    scope: CoroutineScope,
    sources: DataSources<SearchScreen>
): UseCaseResult<SearchScreen> = with(sources.screen.state) {
    ads.loading = true
    val job = scope.launch {
        GetCategoriesUseCase(scope, sources).job?.join()
        GetAdsUseCase(scope, sources, "").job?.join()
        ads.loading = false
    }

    return@with UseCaseResult(sources, scope, job)
}

fun GetCategoriesUseCase(
    scope: CoroutineScope,
    sources: DataSources<SearchScreen>
): UseCaseResult<SearchScreen> = with(sources.screen) {
    state.categories.loading = true
    val job = scope.launch {
        val result = sources.backend.adsService.getCategories()
        val categories = result.getOrNull()
        state.categories.loading = false

        if (categories != null) {
            state.categories.data = categories
        } else {
            state.categories.loadingFailed = true
        }
    }
    return UseCaseResult(sources, scope, job)
}

fun GetAdDetailsUseCase(
    scope: CoroutineScope,
    sources: DataSources<SearchScreen>,
    ad: Ad
): UseCaseResult<SearchScreen> {
    sources.app.currentScreenFlow.tryEmit(
        AdDetailsScreen(ad)
    )

    return UseCaseResult(sources, scope)
}

fun GetAdsUseCase(
    scope: CoroutineScope,
    sources: DataSources<SearchScreen>,
    query: String
): UseCaseResult<SearchScreen> = with(sources.screen.state) {

    if (ads.loading) {
        return@with UseCaseResult(sources, scope)
    }

    if (searchFilter.query != query) {
        adsPage = 0
    }
    searchFilter.query = query

    val job = scope.launch {
        val result = sources.backend.adsService.getAds(page = adsPage, query = query)
        val adsList = result.getOrNull()

        if (result.isSuccess && adsList != null) {
            ads.data = adsList.ads
        } else {
            ads.loadingFailed = true
        }
    }

    return@with UseCaseResult(sources, scope, job)
}