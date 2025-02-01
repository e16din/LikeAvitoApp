package me.likeavitoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.auth.AuthScreenProvider
import me.likeavitoapp.screens.main.MainScreen
import me.likeavitoapp.screens.main.MainScreenProvider
import me.likeavitoapp.screens.splash.SplashScreen
import me.likeavitoapp.screens.splash.SplashScreenProvider
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme

inline fun CoroutineScope.launchWithHandler(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    crossinline launch: suspend () -> Unit
): Job {
    return launch(defaultContext + dispatcher) {
        launch.invoke()
    }
}

class AppViewModel() : ViewModel() {

    fun init(platform: AppPlatform) {
        initMain(platform, viewModelScope)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vm by viewModels<AppViewModel>()
        vm.init(application as AppPlatform)

        enableEdgeToEdge()
        setContent {
            LikeAvitoAppTheme {
                Root()
            }
        }
    }
}


@Composable
fun Root() {
    val screen = appModel.navigator.nextScreen.collectAsState()

    log("Root: " + screen.value.javaClass.simpleName)
    when (screen.value) {
        is SplashScreen -> SplashScreenProvider(screen.value as SplashScreen)
        is AuthScreen -> AuthScreenProvider(screen.value as AuthScreen)
        is MainScreen -> MainScreenProvider(screen.value as MainScreen)
    }
}