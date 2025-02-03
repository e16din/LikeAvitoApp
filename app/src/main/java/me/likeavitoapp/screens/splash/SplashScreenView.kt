package me.likeavitoapp.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.likeavitoapp.model.mockCoroutineScope
import me.likeavitoapp.model.mockDataSource
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.provideRootScreen

import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun SplashScreenProvider(screen: SplashScreen) {
    SplashScreenView(screen)

    val scenariosEnabled = provideRootScreen().state.scenariosEnabled.collectAsState()
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
    LikeAvitoAppTheme {
        SplashScreenView(
            screen = SplashScreen(
                parentNavigator = mockScreensNavigator(),
                scope = mockCoroutineScope(),
                sources = mockDataSource()
            )
        )
    }
}