package me.likeavitoapp.screens.addetails

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import me.likeavitoapp.appModel


@Composable
fun AdDetailsScreenProvider() {

}

@Composable
fun AdDetailsScreenView(screen: AdDetailsScreen) {
    BackHandler {
        appModel.PressBack(screen)
    }
}