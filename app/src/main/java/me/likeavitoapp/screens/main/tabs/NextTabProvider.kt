package me.likeavitoapp.screens.main.tabs

import androidx.compose.runtime.Composable
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.screens.main.tabs.cart.OrdersScreen
import me.likeavitoapp.screens.main.tabs.cart.OrdersScreenProvider
import me.likeavitoapp.screens.main.tabs.favorites.FavoritesScreen
import me.likeavitoapp.screens.main.tabs.favorites.FavoritesScreenProvider
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreen
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreenProvider
import me.likeavitoapp.screens.main.tabs.search.SearchScreen
import me.likeavitoapp.screens.main.tabs.search.SearchScreenProvider

@Composable
fun NextTabProvider(from: IScreen, tabsNavigator: ScreensNavigator) {
    val nextScreen = tabsNavigator.screen.collectAsState()

    with(nextScreen.value) {
        when (this) {
            is SearchScreen -> if (from !is SearchScreen) {
                SearchScreenProvider(this, tabsNavigator)
            }
            is FavoritesScreen -> if (from !is FavoritesScreen) {
                FavoritesScreenProvider(this, tabsNavigator)
            }
            is ProfileScreen -> if (from !is ProfileScreen) {
                ProfileScreenProvider(this, tabsNavigator)
            }
            is OrdersScreen -> if (from !is OrdersScreen) {
                OrdersScreenProvider(this, tabsNavigator)
            }
        }
    }
}