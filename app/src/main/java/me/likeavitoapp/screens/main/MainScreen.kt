package me.likeavitoapp.screens.main

import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.User
import me.likeavitoapp.model.dataSources
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.tabs.cart.CartScreen
import me.likeavitoapp.screens.main.createad.CreateAdScreen
import me.likeavitoapp.screens.main.tabs.favorites.FavoritesScreen
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreen
import me.likeavitoapp.screens.main.tabs.search.SearchScreen


class MainScreen(
    val sources: DataSources = dataSources(),
) : IScreen {

    val state = State()
    val nav = Navigation()

    val navigator = ScreensNavigator(nav.pages.searchScreen())

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
            fun createAdScreen() = CreateAdScreen()
        }
    }

    // UseCases:

    val searchScreen = nav.pages.searchScreen()
    val favoritesScreen = nav.pages.favoritesScreen()
    val profileScreen = nav.pages.profileScreen(sources.app.user!!)
    val cartScreen = nav.pages.cartScreen()


    fun ClickToSearchUseCase() {
        navigator.startScreen(searchScreen)
    }

    fun ClickToFavoritesUseCase() {
        navigator.startScreen(favoritesScreen)
    }

    fun ClickToCreateAdUseCase() {
        sources.app.navigator.startScreen(nav.stack.createAdScreen())
    }

    fun ClickToCartUseCase() {
        navigator.startScreen(cartScreen)
    }

    fun ClickToProfileUseCase() {
        navigator.startScreen(profileScreen)
    }

    fun PressBack() {
        recordScenarioStep()

        with(navigator) {
            if (screens.size > 1) {
                val last = screens.last()
                if (last is SearchScreen) {
                    screens.clear()
                    screens.add(last)

                } else {
                    backToPrevious()
                }
            }
        }
    }
}