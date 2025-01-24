package me.likeavitoapp.screens.main

import me.likeavitoapp.DataSources

class SelectTabUseCases(
    val sources: DataSources<MainScreen>
) {
    fun runWith(tab: MainScreen.Tabs) {
        sources.screen.state.selectedTab = tab
    }
}