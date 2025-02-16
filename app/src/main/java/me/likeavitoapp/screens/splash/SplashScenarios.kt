package me.likeavitoapp.screens.splash

import me.likeavitoapp.get


suspend fun RunAppFromScratchScenario(screen: SplashScreen) {
    get.sources().platform.appDataStore.clear()
    screen.StartScreenUseCase()
}

