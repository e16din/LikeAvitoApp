package me.likeavitoapp.screens.root

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.log
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
    val screen = rootScreen.navigator.screen.collectAsState()
    val loadingEnabled = rootScreen.state.loadingEnabled.collectAsState()

    LaunchedEffect(Unit) {
        rootScreen.StartScreenUseCase()
    }

    Box(modifier = Modifier.pointerInput(Unit) {

        detectTapGestures {
            log("Tap: x = ${it.x}, y = ${it.y}")
        }
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                // handle pointer event
//                if (filter == null || event.type == filter) {
                log("PointerEvent | ${event.type}, ${event.changes.first().position}")
//                }
            }
        }

    }) {
        Box(
            modifier = Modifier
                .systemBarsPadding()
                .navigationBarsPadding()
                .fillMaxSize()
        ) {
            with(screen.value) {
                when (this) {
                    is SplashScreen -> SplashScreenProvider(this)
                    is AuthScreen -> AuthScreenProvider(this)
                    is MainScreen -> MainScreenProvider(this)
                }
            }
        }

        if (loadingEnabled.value) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.72f)
            ) {
                Box {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.Center)
                    )
                }
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