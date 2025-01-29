package me.likeavitoapp.screens.main

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


    class State {}

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
            fun createAdScreen(prevScreen: Screen) = CreateAdScreen(
                prevScreen = prevScreen,
                innerScreen = null
            )
        }
    }

    // UseCases:

    val searchScreen = nav.pages.searchScreen()
    val favoritesScreen = nav.pages.favoritesScreen()
    val profileScreen = nav.pages.profileScreen(sources.app.user!!)
    val cartScreen = nav.pages.cartScreen()

    fun StartScreenUseCase() {
        innerScreen = MutableStateFlow(searchScreen)
    }

    private fun onClickTo(screen: Screen) {
        innerScreen?.value = screen.apply {
            prevScreen = innerScreen?.value?.prevScreen
        }
    }

    fun ClickToSearchUseCase() {
        onClickTo(searchScreen)
    }

    fun ClickToFavoritesUseCase() {
        onClickTo(favoritesScreen)
    }

    fun ClickToCreateAdUseCase() {
        innerScreen?.value = nav.stack.createAdScreen(this)
    }

    fun ClickToCartUseCase() {
        onClickTo(cartScreen)
    }

    fun ClickToProfileUseCase() {
        onClickTo(profileScreen)
    }
}