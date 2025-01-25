package me.likeavitoapp.screens.main.search

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.likeavitoapp.DataSources
import me.likeavitoapp.UseCaseResult


fun ChangeSearchQueryUseCase(
    scope: CoroutineScope,
    sources: DataSources<SearchScreen>,
    newQuery: String,
    justUpdate: Boolean
): UseCaseResult<SearchScreen> = with(sources.screen.state) {
    searchFilter.query = newQuery

    if (justUpdate) {
        return@with UseCaseResult(sources, scope)
    }

    searchTips.loading = true
    var job = scope.launch {
        val result = sources.backend.adsService.getSearchTips(
            categoryId = selectedCategory.id,
            query = newQuery
        )
        searchTips.loading = false
        searchTips.data = result.getOrNull() ?: emptyList()
    }

    return@with UseCaseResult(sources, scope, job)
}

fun SelectSearchTipUseCase(
    scope: CoroutineScope,
    sources: DataSources<SearchScreen>
) {

}

fun SelectCategoryUseCase(
    scope: CoroutineScope,
    sources: DataSources<SearchScreen>
) {

}

fun ChangeSearchFilterUseCase(
    scope: CoroutineScope,
    sources: DataSources<SearchScreen>
) {
}
