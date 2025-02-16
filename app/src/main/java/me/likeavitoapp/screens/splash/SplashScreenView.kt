package me.likeavitoapp.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.get
import me.likeavitoapp.model.mockMainSet

import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun SplashScreenProvider(screen: SplashScreen) {
    SplashScreenView(screen)

    val scenariosEnabled = get.app().rootScreen.state.scenariosEnabled
    LaunchedEffect(Unit) {
        if (scenariosEnabled.value) {
            RunAppFromScratchScenario(screen)

        } else {
            screen.StartScreenUseCase()
        }
    }
}

@Composable
fun SplashScreenView(screen: SplashScreen) {
    val contentEnabled = screen.state.contentEnabled.collectAsState()
    AnimatedVisibility(contentEnabled.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Добро\n\nпожаловать!", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    get = mockMainSet()
    LikeAvitoAppTheme {
        SplashScreenView(
            screen = SplashScreen(
                navigator = mockScreensNavigator(),
            )
        )
    }
}