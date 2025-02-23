package me.likeavitoapp.screens.main

import me.likeavitoapp.className
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.InitialScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.createad.CreateAdScreen
import me.likeavitoapp.screens.main.tabs.TabsRootScreen
import me.likeavitoapp.screens.main.tabs.favorites.FavoritesScreen
import me.likeavitoapp.screens.main.tabs.orders.OrdersScreen
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreen
import me.likeavitoapp.screens.main.tabs.search.SearchScreen


class MainScreen() : IScreen {

    val state = State()

    val navigator = ScreensNavigator(tag = className())

    val searchScreen = SearchScreen(navigator)
    val favoritesScreen = FavoritesScreen(navigator)
    val profileScreen = ProfileScreen(navigator = navigator)
    val ordersScreen = OrdersScreen(navigator)

    val tabsRootScreen = TabsRootScreen()

    var initialTabScreen: IScreen = searchScreen

    class State {}


    // UseCases:

    fun StartScreenUseCase() {
        if (!tabsRootScreen.navigator.hasScreen()) {
            tabsRootScreen.navigator.startScreen(initialTabScreen)
        }
    }

    fun ClickToSearchUseCase() {
        tabsRootScreen.navigator.startScreen(searchScreen)
    }

    fun ClickToFavoritesUseCase() {
        tabsRootScreen.navigator.startScreen(favoritesScreen)
    }

    fun ClickToCreateAdUseCase() {
        navigator.startScreen(CreateAdScreen())
    }

    fun ClickToCartUseCase() {
        tabsRootScreen.navigator.startScreen(ordersScreen)
    }

    fun ClickToProfileUseCase() {
        tabsRootScreen.navigator.startScreen(profileScreen)
    }

    fun PressBackUseCase() {
        recordScenarioStep()

        with(tabsRootScreen.navigator) {
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

    fun returnToOrdersTab() {
        navigator.startScreen(InitialScreen, clearAll = true)
        tabsRootScreen.navigator.startScreen(ordersScreen)
    }
}