package me.likeavitoapp.screens.main.tabs.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import me.likeavitoapp.screens.main.tabs.cart.CartScreen
import me.likeavitoapp.screens.main.tabs.cart.CartScreenProvider
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreen
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreenProvider
import me.likeavitoapp.screens.main.tabs.search.SearchScreen
import me.likeavitoapp.screens.main.tabs.search.SearchScreenProvider


@Composable
fun FavoritesScreenProvider(screen: FavoritesScreen) {
    val nextScreen by screen.navigator.nextScreen.collectAsState()

    Box {
        FavoritesScreenView(screen)

        when (nextScreen) {
            is SearchScreen -> SearchScreenProvider(nextScreen as SearchScreen)
            is ProfileScreen -> ProfileScreenProvider(nextScreen as ProfileScreen)
            is CartScreen -> CartScreenProvider(nextScreen as CartScreen)
        }
    }
}

@Composable
fun FavoritesScreenView(screen: FavoritesScreen) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Green)) {}
}