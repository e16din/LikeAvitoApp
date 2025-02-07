package me.likeavitoapp.screens.main.tabs.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import me.likeavitoapp.screens.main.tabs.NextTabProvider
import me.likeavitoapp.screens.main.tabs.TabsRootScreen


@Composable
fun CartScreenProvider(screen: CartScreen, tabsRootScreen: TabsRootScreen) {

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            CartScreenView(screen)
        }

        NextTabProvider(screen, tabsRootScreen)
    }
}

@Composable
fun CartScreenView(screen: CartScreen) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Blue)) {}
}