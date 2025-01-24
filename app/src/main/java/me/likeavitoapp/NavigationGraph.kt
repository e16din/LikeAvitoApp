package me.likeavitoapp

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import me.likeavitoapp.screens.auth.AuthScreenProvider
import me.likeavitoapp.screens.main.MainScreenProvider
import me.likeavitoapp.screens.main.search.MainScreenPreview
import me.likeavitoapp.screens.main.search.SearchScreenView
import me.likeavitoapp.screens.splash.SplashScreen
import me.likeavitoapp.screens.splash.SplashScreenProvider
import me.likeavitoapp.screens.splash.SplashScreenView



@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NavigationGraph(app: AppModel = AppModel) {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavRoutes.Splash) {
        composable(NavRoutes.Splash) { SplashScreenProvider() }
        composable(NavRoutes.Auth) { AuthScreenProvider() }
        composable(NavRoutes.Main) { MainScreenProvider() }
    }

    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        scope.launch(exceptionHandler) {
            app.currentScreenFlow.collect { screen ->
                app.screens.add(screen)

                if (screen is SplashScreen) {
                    return@collect // NOTE: return because it is started as startDestination
                }

                navController.navigate(screen.route.path) {
                    if (screen.route.isRoot) {
                        val prevRoot = app.screens.firstOrNull()
                        prevRoot?.let {
                            popUpTo(it.route.path) { inclusive = true }
                        }

                        app.screens.clear()
                        app.screens.add(screen)
                    }
                }
            }
        }
    }
}

