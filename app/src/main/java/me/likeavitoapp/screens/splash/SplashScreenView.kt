package me.likeavitoapp.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun SplashScreenView(viewModel: SplashViewModel = viewModel()) {

    LaunchedEffect(Unit) {
        viewModel.onStart()
    }

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