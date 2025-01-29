package me.likeavitoapp.screens.addetails

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import me.likeavitoapp.app


@Composable
fun AdDetailsScreenProvider() {

}

@Composable
fun AdDetailsScreenView(screen: AdDetailsScreen) {
    BackHandler {
        app.PressBack(screen)
    }
}