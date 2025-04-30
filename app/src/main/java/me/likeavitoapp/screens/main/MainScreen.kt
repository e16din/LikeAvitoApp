package me.likeavitoapp.screens.main

import me.likeavitoapp.className
import me.likeavitoapp.get
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.createad.CreateAdScreen
import me.likeavitoapp.screens.main.tabs.TabsRootScreen
import me.likeavitoapp.screens.main.tabs.favorites.FavoritesScreen
import me.likeavitoapp.screens.main.tabs.orders.OrdersScreen
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreen
import me.likeavitoapp.screens.main.tabs.search.SearchScreen


class MainScreen() : IScreen {

    class State

    val state = State()

    val navigator = ScreensNavigator(tag = className())

    val searchScreen = SearchScreen(navigator)
    val favoritesScreen = FavoritesScreen(navigator)
    val profileScreen = ProfileScreen(navigator = navigator)
    val ordersScreen = OrdersScreen(navigator)

    val tabsRootScreen = TabsRootScreen(searchScreen)


    // UseCases:

    fun StartScreenUseCase() {
        recordScenarioStep()

        get.sources().app.updateNewMessagesIndicator()
    }

    fun ClickToSearchUseCase() {
        recordScenarioStep()

        tabsRootScreen.navigator.startScreen(searchScreen)
    }

    fun ClickToFavoritesUseCase() {
        recordScenarioStep()

        tabsRootScreen.navigator.startScreen(favoritesScreen)
    }

    fun ClickToCreateAdUseCase() {
        recordScenarioStep()

        navigator.startScreen(
            CreateAdScreen(navigator)
        )
    }

    fun ClickToCartUseCase() {
        recordScenarioStep()

        tabsRootScreen.navigator.startScreen(ordersScreen)
    }

    fun ClickToProfileUseCase() {
        recordScenarioStep()

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
        navigator.reset()
        tabsRootScreen.navigator.startScreen(ordersScreen)
    }

    fun returnToActiveTab() {
        navigator.reset()
    }
}