package me.likeavitoapp

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import me.likeavitoapp.screens.root.RootScreenView
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


class AppViewModel() : ViewModel()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vm by viewModels<AppViewModel>()

        val app = get.init(application as AppPlatform, vm.viewModelScope)

        val lightTransparentStyle = SystemBarStyle.light(
            scrim = WHITE,
            darkScrim = WHITE
        )
        val darkTransparentStyle = SystemBarStyle.light(
            scrim = BLACK,
            darkScrim = BLACK
        )
        val navigationBarStyle = if (resources.configuration.isNightModeActive)
            darkTransparentStyle
        else
            lightTransparentStyle
        enableEdgeToEdge(
            navigationBarStyle = navigationBarStyle
        )

        setContent {
            LikeAvitoAppTheme {
                RootScreenView(app.rootScreen)
            }
        }
    }
}