package me.likeavitoapp.screens.splash


suspend fun RunAppFromScratchScenario(screen: SplashScreen) {
    screen.sources.platform.appDataStore.clear()
    screen.StartScreenUseCase()
}

