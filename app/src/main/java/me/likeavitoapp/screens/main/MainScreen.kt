package me.likeavitoapp.screens.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.DataSources
import me.likeavitoapp.Screen
import me.likeavitoapp.User
import me.likeavitoapp.dataSources
import me.likeavitoapp.screens.main.cart.CartScreen
import me.likeavitoapp.screens.main.createad.CreateAdScreen
import me.likeavitoapp.screens.main.favorites.FavoritesScreen
import me.likeavitoapp.screens.main.profile.ProfileScreen
import me.likeavitoapp.screens.main.search.SearchScreen


class MainScreen(
    val sources: DataSources = dataSources(),
    override var prevScreen: Screen? = null,
    override var innerScreen: MutableStateFlow<Screen>? = null,
) : Screen {

    val state = State()
    val nav = Navigation()

    init {
        StartScreenUseCase()
    }

    enum class Tabs {
        Search,
        Favorites,
        CreateAd,
        Cart,
        Profile
    }

    class State {
        var selectedTab by mutableStateOf(Tabs.Search)
    }

    class Navigation(
        val roots: Roots = Roots(),
        val pages: Pages = Pages(),
        val stack: Stack = Stack()
    ) {
        class Roots {}

        class Pages {
            fun searchScreen() = SearchScreen()
            fun favoritesScreen() = FavoritesScreen()
            fun cartScreen() = CartScreen()
            fun profileScreen(user: User) = ProfileScreen(user = user)
        }

        class Stack {
            fun createAdScreen(initialScreen: Screen) = CreateAdScreen(
                prevScreen = initialScreen,
                innerScreen = initialScreen
            )
        }
    }

    // UseCases:

    val searchScreen = nav.pages.searchScreen()
    val favoritesScreen = nav.pages.favoritesScreen()
    val profileScreen = nav.pages.profileScreen(sources.app.user!!)
    val cartScreen = nav.pages.cartScreen()

    fun StartScreenUseCase() {
        innerScreen = MutableStateFlow(
            nav.pages.searchScreen()
        )
    }

    fun SelectTabUseCase(tab: Tabs) = with(this) {
        state.selectedTab = tab

        if (tab == Tabs.CreateAd) {
            innerScreen?.value = nav.stack.createAdScreen(this)

        } else {
            innerScreen?.value = when (tab) {
                Tabs.Search -> searchScreen
                Tabs.Favorites -> favoritesScreen
                Tabs.Profile -> profileScreen
                Tabs.Cart -> cartScreen
                else -> throw IllegalArgumentException("Tab key is incorrect.")
            }.apply {
                prevScreen = if (innerScreen?.value is SearchScreen) {
                    null
                } else {
                    innerScreen?.value
                }
            }
        }
    }
}