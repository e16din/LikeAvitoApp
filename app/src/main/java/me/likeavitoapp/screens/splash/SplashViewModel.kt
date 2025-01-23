package me.likeavitoapp.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.likeavitoapp.AuthScreen
import me.likeavitoapp.DataSources
import me.likeavitoapp.MainScreen
import me.likeavitoapp.SplashScreen
import me.likeavitoapp.UserDataSource
import me.likeavitoapp.exceptionHandler


class SplashViewModel: ViewModel() {
    val userDataSource = SplashDataSource()

    private val sources = DataSources(userDataSource)

    private val startUseCase: StartUseCase = StartUseCase(viewModelScope, sources)

    fun onStart() {
        startUseCase.run()
    }
}

class SplashDataSource : UserDataSource

class StartUseCase(
    val scope: CoroutineScope,
    val sources: DataSources<SplashDataSource>
) {
    var startMs: Long = System.currentTimeMillis()

    fun run() {
        scope.launch(exceptionHandler) {
            sources.app.currentScreenFlow.emit(SplashScreen())
            sources.app.user.id = sources.platform.userIdStore.load()

            val finishMs = System.currentTimeMillis()
            val delayMs = 1000 - (finishMs - startMs)
            delay(delayMs)

            val nextScreen = if (sources.app.user.id != null)
                MainScreen()
            else
                AuthScreen()
            sources.app.currentScreenFlow.emit(nextScreen)
        }
    }
}