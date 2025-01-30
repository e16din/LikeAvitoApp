package me.likeavitoapp.screens.splash

import me.likeavitoapp.model.dataSourcesWithScreen


suspend fun RunAppFromScratchScenario() {
    val sources = dataSourcesWithScreen<SplashScreen>()

    sources.platform.appDataStore.clear()
    sources.screen.StartScreenUseCase(System.currentTimeMillis())
}

