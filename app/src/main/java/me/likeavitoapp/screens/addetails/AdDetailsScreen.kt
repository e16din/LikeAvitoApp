package me.likeavitoapp.screens.addetails

import me.likeavitoapp.Ad
import me.likeavitoapp.NavRoutes
import me.likeavitoapp.Route
import me.likeavitoapp.Screen

class AdDetailsScreen(
    val ad: Ad,
    override val route: Route = Route(NavRoutes.AdDetails)
) : Screen