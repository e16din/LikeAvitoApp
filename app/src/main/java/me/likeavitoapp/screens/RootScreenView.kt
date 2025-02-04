package me.likeavitoapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.auth.AuthScreenProvider
import me.likeavitoapp.screens.main.MainScreen
import me.likeavitoapp.screens.main.MainScreenProvider
import me.likeavitoapp.screens.splash.SplashScreen
import me.likeavitoapp.screens.splash.SplashScreenProvider
import me.likeavitoapp.ui.theme.AppTypography
import me.likeavitoapp.ui.theme.onPrimaryDark
import me.likeavitoapp.ui.theme.primaryContainerDark

@Composable
fun RootScreenView(rootScreen: RootScreen) {
    val screen = rootScreen.navigator.screen

    LaunchedEffect(Unit) {
        rootScreen.StartScreenUseCase()
    }

    Box(modifier = Modifier) {
        Box(
            modifier = Modifier
                .systemBarsPadding()
                .navigationBarsPadding()
                .fillMaxSize()
        ) {
            when (screen.value) {
                is SplashScreen -> SplashScreenProvider(screen.value as SplashScreen)
                is AuthScreen -> AuthScreenProvider(screen.value as AuthScreen)
                is MainScreen -> MainScreenProvider(screen.value as MainScreen)
            }
        }

        if (rootScreen.state.demoLabelEnabled) {
            Text(
                modifier = Modifier
                    .padding(vertical = 32.dp, horizontal = 24.dp)
                    .clip(CircleShape)
                    .clickable {
                        rootScreen.ClickToDemoDeveloperUseCase()
                    }
                    .background(primaryContainerDark)
                    .padding(8.dp)
                    .align(Alignment.TopEnd),
                text = "Demo",
                color = onPrimaryDark,
                style = AppTypography.labelSmall
            )
        }
    }
}