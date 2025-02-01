package me.likeavitoapp.screens.main

import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
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

    val navigator = ScreensNavigator(tag = javaClass.simpleName)

    val searchScreen = SearchScreen(parentNavigator = navigator)
    val favoritesScreen = FavoritesScreen(parentNavigator = navigator)
    val profileScreen = ProfileScreen(user = sources.app.user!!)
    val cartScreen = CartScreen()

    val tabsNavigator =
        ScreensNavigator(initialScreen = searchScreen, tag = javaClass.simpleName + "Tab").apply {
            searchScreen.navigator = this
            favoritesScreen.navigator = this
            profileScreen.navigator = this
            cartScreen.navigator = this
        }

    val mainTabScreen = searchScreen

    class State {}


    // UseCases:

    fun ClickToSearchUseCase() {
        tabsNavigator.startScreen(searchScreen)
    }

    fun ClickToFavoritesUseCase() {
        tabsNavigator.startScreen(favoritesScreen)
    }

    fun ClickToCreateAdUseCase() {
        sources.app.navigator.startScreen(CreateAdScreen())
    }

    fun ClickToCartUseCase() {
        tabsNavigator.startScreen(cartScreen)
    }

    fun ClickToProfileUseCase() {
        tabsNavigator.startScreen(profileScreen)
    }

    fun PressBack() {
        recordScenarioStep()

        with(tabsNavigator) {
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