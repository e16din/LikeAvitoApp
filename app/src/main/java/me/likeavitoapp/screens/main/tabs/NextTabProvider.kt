package me.likeavitoapp.screens.main.tabs

import androidx.compose.runtime.Composable
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.screens.main.tabs.cart.CartScreen
import me.likeavitoapp.screens.main.tabs.cart.CartScreenProvider
import me.likeavitoapp.screens.main.tabs.favorites.FavoritesScreen
import me.likeavitoapp.screens.main.tabs.favorites.FavoritesScreenProvider
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreen
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreenProvider
import me.likeavitoapp.screens.main.tabs.search.SearchScreen
import me.likeavitoapp.screens.main.tabs.search.SearchScreenProvider

@Composable
fun NextTabProvider(from: IScreen, tabsRootScreen: TabsRootScreen) {
    val nextScreen = tabsRootScreen.navigator.screen.collectAsState()

    with(nextScreen.value) {
        when (this) {
            is SearchScreen -> if (from !is SearchScreen) {
                SearchScreenProvider(this, tabsRootScreen)
            }
            is FavoritesScreen -> if (from !is FavoritesScreen) {
                FavoritesScreenProvider(this, tabsRootScreen)
            }
            is ProfileScreen -> if (from !is ProfileScreen) {
                ProfileScreenProvider(this, tabsRootScreen)
            }
            is CartScreen -> if (from !is CartScreen) {
                CartScreenProvider(this, tabsRootScreen)
            }
        }
    }
}