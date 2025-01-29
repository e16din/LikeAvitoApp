package me.likeavitoapp.screens.splash

import me.likeavitoapp.dataSourcesWithScreen


suspend fun RunAppFromScratchScenario() {
    val sources = dataSourcesWithScreen<SplashScreen>()

    sources.platform.authDataStore.clear()
    sources.screen.StartScreenUseCase(System.currentTimeMillis())
}

