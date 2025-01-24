package me.likeavitoapp.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import me.likeavitoapp.DataSources
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun SplashScreenProvider() {
    val scope = rememberCoroutineScope()

    val sources = DataSources<SplashScreen>()

    val startUseCase = StartUseCase(scope, sources)

    SplashScreenView()

    LaunchedEffect(Unit) {
        startUseCase.run()
    }
}

@Composable
fun SplashScreenView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Splash Screen", style = MaterialTheme.typography.headlineMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    LikeAvitoAppTheme {
        SplashScreenView()
    }
}