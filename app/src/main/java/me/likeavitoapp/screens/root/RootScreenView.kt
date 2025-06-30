package me.likeavitoapp.screens.root

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.likeavitoapp.R
import me.likeavitoapp.get
import me.likeavitoapp.model.collectAsState
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
fun RootScreenView(screen: RootScreen) {
    with(get.sources().platform) {
        screenWidthDp = LocalConfiguration.current.screenWidthDp
        screenHeightDp = LocalConfiguration.current.screenHeightDp
    }

    val nextScreen by screen.navigator.screen.collectAsState()
    val loadingEnabled by get.sources().app.loading.collectAsState()
    val message by get.sources().app.message.collectAsState()

    LaunchedEffect(Unit) {
        screen.StartScreenUseCase()
    }

    Box(modifier = Modifier) {
        Box(
            modifier = Modifier
                .systemBarsPadding()
                .navigationBarsPadding()
                .fillMaxSize()
        ) {
            with(nextScreen) {
                when (this) {
                    is SplashScreen -> SplashScreenProvider(this)
                    is AuthScreen -> AuthScreenProvider(this)
                    is MainScreen -> MainScreenProvider(this)
                }
            }
        }

        if (loadingEnabled) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
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

        if (message != null) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .imePadding()
                    .alpha(0.96f)
            ) {
                Box {
                    Text(
                        text = message!!,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    OutlinedButton(
                        onClick = {
                            screen.ClickToLoadingFailedOkUseCase()
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(24.dp)
                    ) {
                        Text(stringResource(R.string.ok))
                    }
                }
            }
        }

        if (screen.state.demoLabelEnabled) {
            Text(
                modifier = Modifier
                    .padding(vertical = 32.dp, horizontal = 24.dp)
                    .clip(CircleShape)
                    .clickable {
                        screen.ClickToDemoDeveloperUseCase()
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