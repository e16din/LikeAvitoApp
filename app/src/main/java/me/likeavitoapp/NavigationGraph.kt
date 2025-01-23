package me.likeavitoapp

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import me.likeavitoapp.screens.auth.AuthScreenView
import me.likeavitoapp.screens.main.MainScreenView
import me.likeavitoapp.screens.splash.SplashScreenView


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NavigationGraph(app: AppModel = AppModel) {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavRoutes.Splash) {
        composable(NavRoutes.Splash) { SplashScreenView() }
        composable(NavRoutes.Auth) { AuthScreenView() }
        composable(NavRoutes.Main) { MainScreenView() }
    }

    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        scope.launch(exceptionHandler) {
            app.currentScreenFlow.collect { screen ->
                app.screens.add(screen)

                if (screen is SplashScreen) {
                    return@collect // NOTE: return because it is started as startDestination
                }

                navController.navigate(screen.route) {
                    if (screen.isRoot) {
                        val prevRoot = app.screens.firstOrNull()
                        prevRoot?.let {
                            popUpTo(it.route) { inclusive = true }
                            app.screens.clear()
                            app.screens.add(screen)
                        }
                    }
                }
            }
        }
    }
}

