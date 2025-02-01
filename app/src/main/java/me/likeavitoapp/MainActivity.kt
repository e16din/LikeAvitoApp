package me.likeavitoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.auth.AuthScreenProvider
import me.likeavitoapp.screens.main.MainScreen
import me.likeavitoapp.screens.main.MainScreenProvider
import me.likeavitoapp.screens.main.addetails.AdDetailsScreen
import me.likeavitoapp.screens.main.addetails.AdDetailsScreenProvider
import me.likeavitoapp.screens.splash.SplashScreen
import me.likeavitoapp.screens.splash.SplashScreenProvider
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    when (screen.value){
        is SplashScreen -> SplashScreenProvider(screen.value as SplashScreen)
        is AuthScreen -> AuthScreenProvider(screen.value as AuthScreen)
        is MainScreen -> MainScreenProvider(screen.value as MainScreen)
        is AdDetailsScreen -> AdDetailsScreenProvider(screen.value as AdDetailsScreen)
    }
}