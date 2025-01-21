package me.likeavitoapp

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
        scope.launch {
            app.screens.collect { item ->
                if (item is SplashScreen) {
                    return@collect // NOTE: return because it is started as startDestination
                }

                navController.navigate(item.route) {
                    if (item.isRoot && app.screens.replayCache.isNotEmpty()) {
                        println("popUp")
                        val prevRoot = app.screens.replayCache.first()
                        popUpTo(prevRoot.route) { inclusive = true }
                    }
                }
            }
        }
    }
}


