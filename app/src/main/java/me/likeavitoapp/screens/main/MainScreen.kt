package me.likeavitoapp.screens.main

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.StubScreen
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.tabs.cart.CartScreen
import me.likeavitoapp.screens.main.createad.CreateAdScreen
import me.likeavitoapp.screens.main.tabs.TabsRootScreen
import me.likeavitoapp.screens.main.tabs.favorites.FavoritesScreen
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreen
import me.likeavitoapp.screens.main.tabs.search.SearchScreen


class MainScreen(
    val scope: CoroutineScope = provideCoroutineScope(),
    val sources: DataSources = provideDataSources()
) : IScreen {

    val state = State()

    val navigator = ScreensNavigator(tag = javaClass.simpleName)

    val searchScreen = SearchScreen(parentNavigator = navigator)
    val favoritesScreen = FavoritesScreen(parentNavigator = navigator)
    val profileScreen = ProfileScreen(parentNavigator = navigator)
    val cartScreen = CartScreen(parentNavigator = navigator)

//    val tabsNavigator =
//        ScreensNavigator(initialScreen = searchScreen, tag = javaClass.simpleName + "Tab").apply {
//            searchScreen.tabsNavigator = this
//            favoritesScreen.tabsNavigator = this
//            profileScreen.tabsNavigator = this
//            cartScreen.tabsNavigator = this
//        }

    val tabsRootScreen = TabsRootScreen(scope, sources)

    class State {}


    // UseCases:

    fun StartScreenUseCase() {
        if (!tabsRootScreen.navigator.hasScreen()) {
            tabsRootScreen.navigator.startScreen(searchScreen)
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
        tabsRootScreen.navigator.startScreen(cartScreen)
    }

    fun ClickToProfileUseCase() {
        tabsRootScreen.navigator.startScreen(profileScreen)
    }

    fun PressBack() {
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
}