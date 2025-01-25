package me.likeavitoapp.screens.main

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.DataSources
import me.likeavitoapp.UseCaseResult

fun SelectTabUseCases(
    scope: CoroutineScope,
    sources: DataSources<MainScreen>,
    tab: MainScreen.Tabs
): UseCaseResult<MainScreen> = with(sources.screen.state) {
    selectedTab = tab
    navHistory.add(tab)

    return UseCaseResult(sources, scope)
}

fun PressBackUseCases(
    scope: CoroutineScope,
    sources: DataSources<MainScreen>
): UseCaseResult<MainScreen> = with(sources.screen.state) {

    if (navHistory.isNotEmpty()) {
        navHistory.removeAt(navHistory.lastIndex)
        navHistory.lastOrNull()?.let {
            selectedTab = it
        }
    }

    return UseCaseResult(sources, scope)
}