package me.likeavitoapp.screens.main.tabs.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import me.likeavitoapp.screens.main.tabs.favorites.FavoritesScreen
import me.likeavitoapp.screens.main.tabs.favorites.FavoritesScreenProvider
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreen
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreenProvider
import me.likeavitoapp.screens.main.tabs.search.SearchScreen
import me.likeavitoapp.screens.main.tabs.search.SearchScreenProvider


@Composable
fun CartScreenProvider(screen: CartScreen) {
    val nextScreen by screen.tabsNavigator.screen

    Box {
        CartScreenView(screen)

        when (nextScreen) {
            is SearchScreen -> SearchScreenProvider(nextScreen as SearchScreen)
            is FavoritesScreen -> FavoritesScreenProvider(nextScreen as FavoritesScreen)
            is ProfileScreen -> ProfileScreenProvider(nextScreen as ProfileScreen)
        }
    }
}

@Composable
fun CartScreenView(screen: CartScreen) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Blue)) {}
}