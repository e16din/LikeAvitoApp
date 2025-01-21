package me.likeavitoapp.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.likeavitoapp.AppModel
import me.likeavitoapp.AppPlatform
import me.likeavitoapp.AuthScreen
import me.likeavitoapp.MainScreen
import me.likeavitoapp.SplashScreen
import me.likeavitoapp.screens.splash.SplashViewModel


class SplashViewModel(
    val startUseCase: StartUseCase = StartUseCase()
) : ViewModel() {

    fun onStart() {
        viewModelScope.launch {
            startUseCase.run()
        }
    }
}

class StartUseCase(
    val app: AppModel = AppModel,
    val platform: AppPlatform = AppPlatform.get
) {
    var startMs: Long = System.currentTimeMillis()

    suspend fun run() {
        app.screens.emit(SplashScreen())
        app.user.id = platform.userIdStore.load()

        val finishMs = System.currentTimeMillis()
        val delayMs = 1000 - (finishMs - startMs)
        delay(delayMs)

        val nextScreen = if (app.user.id != null)
            MainScreen()
        else
            AuthScreen()
        app.screens.emit(nextScreen)
    }
}